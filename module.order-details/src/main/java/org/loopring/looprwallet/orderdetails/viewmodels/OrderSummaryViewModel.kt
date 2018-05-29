package org.loopring.looprwallet.orderdetails.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: A view-model for showing a single [AppLooprOrder] based on its order hash.
 *
 */
class OrderSummaryViewModel : OfflineFirstViewModel<AppLooprOrder, String>() {

    override val repository = LooprOrderRepository()

    override val waitTime: Long
        get() = 5 * 1000L

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    fun getOrderByHash(owner: ViewLifecycleFragment, orderHash: String, onChange: (AppLooprOrder) -> Unit) {
        initializeData(owner, orderHash, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<AppLooprOrder> {
        return repository.getOrderByHash(OrderSummaryFilter(orderHash = parameter))
    }

    override fun getDataFromNetwork(parameter: String): Deferred<AppLooprOrder> {
        return service.getOrderByHash(parameter)
    }

    override fun addNetworkDataToRepository(data: AppLooprOrder, parameter: String) {
        repository.add(data)
    }

    override fun isRefreshNecessary(parameter: String): Boolean {
        val time = data?.lastUpdated?.time ?: return true

        return isRefreshNecessaryBasedOnDate(time)
    }

    override fun addSyncDataToRepository(parameter: String) {
        // NO OP - We use the item itself instead of the sync repository
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_NONE
}