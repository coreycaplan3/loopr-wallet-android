package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.core.models.order.OrderFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.wallet.LooprWallet
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
class GeneralOrdersViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<LooprOrder>, OrderFilter>() {

    // 5 seconds in ms
    override val waitTime = 5 * 1000L

    override val repository = LooprOrderRepository(currentWallet)

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    /**
     * Gets the user's orders based on the provided [filter].
     */
    fun getOrders(owner: ViewLifecycleFragment, filter: OrderFilter, onChange: (OrderedRealmCollection<LooprOrder>) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: OrderFilter): LiveData<OrderedRealmCollection<LooprOrder>> {
        return repository.getOrders(parameter)
    }

    override fun isRefreshNecessary(parameter: OrderFilter) = defaultIsRefreshNecessary(parameter.address)

    override fun getDataFromNetwork(parameter: OrderFilter): Deferred<OrderedRealmCollection<LooprOrder>> {
        return service.getOrdersByAddress(parameter.address)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprOrder>) {
        repository.addList(data)
    }

    override fun addSyncDataToRepository(parameter: OrderFilter) {
        syncRepository.add(SyncData(syncType, parameter.address, Date()))
    }

    override val syncType
        get() = when (mParameter?.statusFilter) {
            OrderFilter.FILTER_OPEN_ALL -> SyncData.SYNC_TYPE_ORDERS_OPEN
            OrderFilter.FILTER_FILLED -> SyncData.SYNC_TYPE_ORDERS_FILLED
            OrderFilter.FILTER_CANCELLED -> SyncData.SYNC_TYPE_ORDERS_CANCELLED
            else -> SyncData.SYNC_TYPE_NONE
        }

}