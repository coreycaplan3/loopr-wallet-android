package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.OrderFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOrderViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<Any, OrderFilter>() {

    // 5 seconds in ms
    override val waitTime = 5 * 1000L

    override val repository = LooprOrderRepository(currentWallet)

    fun getOpenOrders(filter: OrderFilter, ticker: String? = null) {
        TODO("not implemented")
    }

    fun getFilledOrders(filter: OrderFilter, ticker: String? = null) {
        TODO("not implemented")
    }

    fun getCancelledOrders(filter: OrderFilter, ticker: String? = null) {
        TODO("not implemented")
    }

    override fun getLiveDataFromRepository(parameter: OrderFilter): LiveData<Any> {
        TODO("not implemented")
    }

    override fun isRefreshNecessary(parameter: OrderFilter) = isRefreshNecessaryDefault(parameter.address)

    override fun getDataFromNetwork(parameter: OrderFilter): Deferred<Any> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: Any) {
        TODO("not implemented")
    }

    override fun addSyncDataToRepository(parameter: OrderFilter) {
        syncRepository.add(SyncData(syncType, null, Date()))
    }

    override val syncType
        get() = when (parameter?.address) {
            OrderFilter.FILTER_OPEN_ALL -> SyncData.SYNC_TYPE_ORDERS_OPEN
            OrderFilter.FILTER_FILLED -> SyncData.SYNC_TYPE_ORDERS_FILLED
            OrderFilter.FILTER_CANCELLED -> SyncData.SYNC_TYPE_ORDERS_CANCELLED
            else -> SyncData.SYNC_TYPE_NONE
        }

}