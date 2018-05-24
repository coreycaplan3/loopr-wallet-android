package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderSummaryFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
import org.loopring.looprwallet.core.repositories.loopr.LooprOrderRepository
import org.loopring.looprwallet.core.utilities.RealmUtility
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
class HomeOrdersViewModel : OfflineFirstViewModel<LooprOrderContainer, OrderSummaryFilter>() {

    override val waitTime = 5 * 1000L // 5 seconds in ms

    override val repository = LooprOrderRepository()

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    /**
     * Gets the user's orders based on the provided [filter].
     */
    fun getOrders(owner: ViewLifecycleFragment, filter: OrderSummaryFilter, onChange: (LooprOrderContainer) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun isPredicatesEqual(oldParameter: OrderSummaryFilter?, newParameter: OrderSummaryFilter): Boolean {
        return oldParameter?.address == newParameter.address &&
                oldParameter?.market == newParameter.market &&
                oldParameter?.status == newParameter.status
    }

    override fun getLiveDataFromRepository(parameter: OrderSummaryFilter): LiveData<LooprOrderContainer> {
        return repository.getOrderContainer(parameter)
                .also {
                    it.orderList = repository.getOrders(parameter)
                }
                .asLiveData()
    }

    override fun isRefreshNecessary(parameter: OrderSummaryFilter): Boolean {
        val oldParameter = mParameter
        if (oldParameter != null && oldParameter.pageNumber != parameter.pageNumber) {
            // We're loading a new page
            return true
        }

        return defaultIsRefreshNecessary(parameter.address!!)
    }

    override fun getDataFromNetwork(parameter: OrderSummaryFilter): Deferred<LooprOrderContainer> {
        val result = service.getOrdersByAddress(parameter)

        if (parameter.pageNumber == 1) {
            // Clear all old ones since our paging may be in an inconsistent state
            val list = repository.getOrdersByFilterNow(parameter, IO)
            repository.remove(list, IO)
        }

        return result
    }

    override fun addNetworkDataToRepository(data: LooprOrderContainer, parameter: OrderSummaryFilter) {
        val criteria = LooprOrderContainer.createCriteria(parameter)
        val repository = LooprOrderRepository()
        val oldContainer = repository.getOrderContainerByKeyNow(criteria, IO)
        RealmUtility.diffContainerAndAddToRepository(repository, oldContainer, data) { oldOrder, newOrder ->
            oldOrder.orderHash == newOrder.orderHash
        }
    }

    override fun addSyncDataToRepository(parameter: OrderSummaryFilter) {
        syncRepository.add(SyncData(syncType, parameter.address, Date()))
    }

    override val syncType
        get() = when (mParameter?.status) {
            OrderSummaryFilter.FILTER_OPEN_ALL -> SyncData.SYNC_TYPE_ORDERS_OPEN
            OrderSummaryFilter.FILTER_FILLED -> SyncData.SYNC_TYPE_ORDERS_FILLED
            OrderSummaryFilter.FILTER_CANCELLED -> SyncData.SYNC_TYPE_ORDERS_CANCELLED
            else -> SyncData.SYNC_TYPE_NONE
        }

}