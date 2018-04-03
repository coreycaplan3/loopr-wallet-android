package org.loopring.looprwallet.core.viewmodels.eth

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.user.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.ethplorer.EthplorerService
import com.caplaninnovations.looprwallet.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.repositories.sync.SyncRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import io.realm.Realm
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthTokenBalanceViewModel(private val currentWallet: LooprWallet) : OfflineFirstViewModel<List<EthToken>, String>() {

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    override val syncType = SyncData.SYNC_TYPE_TOKEN_BALANCE

    override val repository = EthTokenRepository(currentWallet)

    private val ethplorerService = EthplorerService.getInstance()

    fun getEthBalanceNow() = repository.getEthNow()

    fun getAllTokensWithBalances(
            owner: LifecycleOwner,
            address: String,
            onChange: (List<CryptoToken>) -> Unit
    ) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<List<EthToken>> {
        return repository.getAllTokens()
    }

    override fun isRefreshNecessary(parameter: String): Boolean {
        val address = currentWallet.credentials.address
        val dateLastSynced = syncRepository.getLastSyncTimeForWallet(address, syncType)?.time
                ?: return true

        return isRefreshNecessary(dateLastSynced)
    }

    override fun getDataFromNetwork(parameter: String): Deferred<List<EthToken>> {
        return ethplorerService.getAddressInfo(currentWallet.credentials.address)
    }

    override fun addNetworkDataToRepository(data: List<EthToken>) {
        repository.runSharedTransaction(Realm.Transaction { realm ->
            val list = data.mapNotNull(this::addTokenInfoToRealm)
            realm.insertOrUpdate(list)
        })
    }

    override fun addSyncDataToRepository() {
        val address = currentWallet.credentials.address
        syncRepository.add(SyncData(syncType, address, Date()))
    }

    // MARK - Private Methods

    /**
     * Persists the given [newToken] to realm if it already exists. If not, it's information is
     * retrieved from the network and it is persisted to realm so it may be reused and accessed.
     */
    private fun addTokenInfoToRealm(newToken: EthToken): EthToken? {
        val address = currentWallet.credentials.address

        val tokenBalance = newToken.tokenBalances[0]
        val token = repository.getTokenByContractAddressNow(contractAddress = newToken.contractAddress)
        val tokenWithBalanceInfo = repository.getTokenByContractAddressAndAddressNow(
                contractAddress = newToken.contractAddress,
                walletAddress = address
        )

        if (token != null) {
            when {
                tokenWithBalanceInfo != null ->
                    // The token in Realm currently has a balance associated with it; update it
                    token.tokenBalances.update(tokenBalance) { it.address == address }
                else ->
                    // The token in Realm DOES NOT have a balance associated with it; insert it
                    token.tokenBalances.add(tokenBalance)
            }
            return token
        } else {
            // There is no token in the database currently for this contract address
            // This should rarely ever happen (but can occur if the user synced tokens that are not
            // in this app by default)
            val tokenRetrieverViewModel = TokenRetrieverViewModel(currentWallet)
            val isSuccessful = runBlocking {
                tokenRetrieverViewModel.getTokenInfoFromNetworkAndAdd(newToken.contractAddress)
                        .await()
            }
            return when {
                isSuccessful -> addTokenInfoToRealm(newToken)
                else -> null
            }
        }
    }

}