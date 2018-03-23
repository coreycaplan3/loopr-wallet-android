package com.caplaninnovations.looprwallet.viewmodels.wallet

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.user.SyncData
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.ethplorer.EthplorerApiService
import com.caplaninnovations.looprwallet.repositories.eth.EthTokenRepository
import com.caplaninnovations.looprwallet.repositories.sync.SyncRepository
import com.caplaninnovations.looprwallet.viewmodels.OfflineFirstViewModel
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class TokenBalanceViewModel(private val currentWallet: LooprWallet) : OfflineFirstViewModel<List<CryptoToken>, String>() {

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    override val syncType = SyncData.SYNC_TYPE_TOKEN_BALANCE

    override val repository = EthTokenRepository(currentWallet)

    private val ethplorerApiService = EthplorerApiService.getInstance()

    fun getEthBalanceNow() = repository.getEthNow()

    fun getAllTokensWithBalances(
            owner: LifecycleOwner,
            address: String,
            onChange: (List<CryptoToken>) -> Unit
    ) {
        initializeData(owner, address, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<List<CryptoToken>> {
        return repository.getAllTokens()
    }

    override fun getDataFromNetwork(parameter: String): Deferred<List<CryptoToken>> {
        return ethplorerApiService.getAddressInfo(currentWallet.credentials.address)
    }

    override fun addNetworkDataToRepository(data: List<CryptoToken>) {
        repository.addList(data)
    }

}