package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.android.architecture.NET
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
class HomeOrdersViewModel : OfflineFirstViewModel<OrderedRealmCollection<LooprOrderContainer>, OrderSummaryFilter>() {

    override val waitTime = 5 * 1000L // 5 seconds in ms

    override val repository = LooprOrderRepository()

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    /**
     * Gets the user's orders based on the provided [filter].
     */
    fun getOrders(owner: ViewLifecycleFragment, filter: OrderSummaryFilter, onChange: (LooprOrderContainer) -> Unit) {
        initializeData(owner, filter) {
            it.firstOrNull()?.let { container ->
                container.orderList = repository.getOrders(filter)
                onChange(container)
            }
        }
    }

    override fun isPredicatesEqual(oldParameter: OrderSummaryFilter?, newParameter: OrderSummaryFilter): Boolean {
        return oldParameter?.address == newParameter.address &&
                oldParameter?.market == newParameter.market &&
                oldParameter?.status == newParameter.status
    }

    override fun getLiveDataFromRepository(parameter: OrderSummaryFilter): LiveData<OrderedRealmCollection<LooprOrderContainer>> {
        return repository.getOrderContainer(parameter)
    }

    override fun isRefreshNecessary(parameter: OrderSummaryFilter): Boolean {
        if (parameter.pageNumber != 1) {
            // We're loading a new page
            return true
        }

        return defaultIsRefreshNecessary(parameter.address!!)
    }

    override fun getDataFromNetwork(parameter: OrderSummaryFilter) = async(NET) {
        val container = service.getOrdersByAddress(parameter).await()
        RealmList(container)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprOrderContainer>, parameter: OrderSummaryFilter) {
        val repository = LooprOrderRepository()
        val oldContainer = repository.getOrderContainerNow(parameter, IO)
        val newContainer = data.first()!!

        RealmUtility.updatePagingContainer(repository, parameter.pageNumber, oldContainer, newContainer)
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