package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.repositories.sync.SyncRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOrderViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<Any, Any>() {

    override val repository = LooprOrderRepository(currentWallet)

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    fun getOpenOrders(filter: Any) {
        TODO("not implemented")
    }

    fun getClosedOrders(filter: Any) {
        TODO("not implemented")
    }

    override fun getLiveDataFromRepository(parameter: Any): LiveData<Any> {
        TODO("not implemented")
    }

    /**
     * It's always necessary to refresh
     */
    override fun isRefreshNecessary(parameter: Any) = true

    override fun getDataFromNetwork(parameter: Any): Deferred<Any> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: Any) {
        TODO("not implemented")
    }

    override val syncType = SyncData.SYNC_TYPE_NONE
}