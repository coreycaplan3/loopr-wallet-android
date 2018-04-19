package org.loopring.looprwallet.core.viewmodels.eth

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.update
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.ethplorer.EthplorerService
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthTokenBalanceViewModel : OfflineFirstViewModel<List<EthToken>, String>() {

    override val syncType = SyncData.SYNC_TYPE_TOKEN_BALANCE

    override val repository = EthTokenRepository()

    private val ethplorerService = EthplorerService.getInstance()

    fun getEthBalanceNow() = repository.getEthNow()

    @Suppress("UNCHECKED_CAST")
    fun getAllTokensWithBalances(
            owner: LifecycleOwner,
            address: String,
            onChange: (RealmResults<CryptoToken>) -> Unit
    ) {
        initializeData(owner, address, onChange as (List<CryptoToken>) -> Unit)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<List<EthToken>> {
        return repository.getAllTokens()
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun getDataFromNetwork(parameter: String): Deferred<List<EthToken>> {
        return ethplorerService.getAddressInfo(parameter)
    }

    override fun addNetworkDataToRepository(data: List<EthToken>) {
        repository.runTransaction(Realm.Transaction { realm ->
            val list = data.mapNotNull(this::addTokenInfoToRealm)
            realm.insertOrUpdate(list)
        })
    }

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    // MARK - Private Methods

    /**
     * Persists the given [newToken] to realm if it already exists. If not, it's information is
     * retrieved from the network and it is persisted to realm so it may be reused and accessed.
     */
    private fun addTokenInfoToRealm(newToken: EthToken): EthToken? {
        val address = parameter ?: return null

        val tokenBalance = newToken.tokenBalances[0]
        val token = repository.getTokenByContractAddressNow(contractAddress = newToken.contractAddress)
        val tokenWithBalanceInfo = repository.getTokenByContractAddressAndAddressNow(
                contractAddress = newToken.contractAddress,
                walletAddress = address
        )

        if (token != null) {
            // The token exists already
            when {
                tokenWithBalanceInfo != null ->
                    // The token is in Realm and has a balance associated with it; update it
                    token.tokenBalances.update(tokenBalance) { it.address == address }
                else ->
                    // The token is in Realm but DOES NOT have a balance associated with it; insert it
                    token.tokenBalances.add(tokenBalance)
            }

            repository.add(token)

            return token
        } else {
            // There is no token in the Realm currently for this contract address
            // This should rarely ever happen but can occur. For example, if the user synced tokens
            // that are not in this app by already
            val tokenRetrieverViewModel = TokenRetrieverViewModel()
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