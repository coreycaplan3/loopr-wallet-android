package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.OrderFilter
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
class GeneralOrderViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<Any, OrderFilter>() {

    override val repository = LooprOrderRepository(currentWallet)

    override val syncRepository = SyncRepository.getInstance(currentWallet)

    fun getOpenOrders(filter: OrderFilter) {
        TODO("not implemented")
    }

    fun getClosedOrders(filter: OrderFilter) {
        TODO("not implemented")
    }

    override fun getLiveDataFromRepository(parameter: OrderFilter): LiveData<Any> {
        TODO("not implemented")
    }

    /**
     * It's always necessary to refresh since we're dealing with dynamic and changing order books
     */
    override fun isRefreshNecessary(parameter: OrderFilter) = true

    override fun getDataFromNetwork(parameter: OrderFilter): Deferred<Any> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: Any) {
        TODO("not implemented")
    }

    override val syncType = SyncData.SYNC_TYPE_NONE

}