package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.extensions.update
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
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
class HomeOrdersViewModel : OfflineFirstViewModel<LooprOrderContainer, OrderFilter>() {

    // 5 seconds in ms
    override val waitTime = 5 * 1000L

    override val repository = LooprOrderRepository()

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    /**
     * Gets the user's orders based on the provided [filter].
     */
    fun getOrders(owner: ViewLifecycleFragment, filter: OrderFilter, onChange: (LooprOrderContainer) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun isPredicatesEqual(oldParameter: OrderFilter?, newParameter: OrderFilter): Boolean {
        return oldParameter?.address == newParameter.address &&
                oldParameter?.market == newParameter.market &&
                oldParameter?.status == newParameter.status
    }

    override fun getLiveDataFromRepository(parameter: OrderFilter): LiveData<LooprOrderContainer> {
        return repository.getOrders(parameter)
    }

    override fun isRefreshNecessary(parameter: OrderFilter): Boolean {
        val oldParameter = mParameter
        if (oldParameter != null && oldParameter.pageNumber != parameter.pageNumber) {
            // We're loading a new page
            return true
        }

        return defaultIsRefreshNecessary(parameter.address!!)
    }

    override fun getDataFromNetwork(parameter: OrderFilter): Deferred<LooprOrderContainer> {
        return service.getOrdersByAddress(parameter)
    }

    override fun addNetworkDataToRepository(data: LooprOrderContainer, parameter: OrderFilter) {
        val criteria = LooprOrderContainer.createCriteria(parameter)
        val container = repository.getOrderContainerByKeyNow(criteria, IO)

        when {
            container != null -> {
                val pagingItem = data.pagingItems.first()
                val didUpdateInPlace = container.pagingItems.update(pagingItem) {
                    it.criteria == data.criteria
                }

                if (!didUpdateInPlace) {
                    container.pagingItems.add(pagingItem)
                }
                repository.add(container)
            }

            else -> repository.add(data)
        }
    }

    override fun addSyncDataToRepository(parameter: OrderFilter) {
        syncRepository.add(SyncData(syncType, parameter.address, Date()))
    }

    override val syncType
        get() = when (mParameter?.status) {
            OrderFilter.FILTER_OPEN_ALL -> SyncData.SYNC_TYPE_ORDERS_OPEN
            OrderFilter.FILTER_FILLED -> SyncData.SYNC_TYPE_ORDERS_FILLED
            OrderFilter.FILTER_CANCELLED -> SyncData.SYNC_TYPE_ORDERS_CANCELLED
            else -> SyncData.SYNC_TYPE_NONE
        }

}