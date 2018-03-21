package com.caplaninnovations.looprwallet.viewmodels.wallet

import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.networking.ethplorer.EthplorerApiService
import com.caplaninnovations.looprwallet.repositories.eth.EthTokenRepository
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

    private var lastRefreshTime: Long? = null

    override val repository = EthTokenRepository(currentWallet)

    private val ethplorerApiService = EthplorerApiService.getInstance()

    override fun getLiveDataFromRepository(parameter: String): LiveData<List<CryptoToken>> {
        return repository.getAllTokens()
    }

    override fun getDataFromNetwork(parameter: String): Deferred<List<CryptoToken>> {
        return ethplorerApiService.getAddressInfo(currentWallet.credentials.address)
    }

    override fun addNetworkDataToRepository(data: List<CryptoToken>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(): Boolean {
        val lastRefreshTime = lastRefreshTime ?: return true

        return isDefaultRefreshNecessary(lastRefreshTime, DEFAULT_WAIT_TIME_MILLIS)
    }

}