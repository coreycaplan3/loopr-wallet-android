package org.loopring.looprwallet.orderdetails.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.orders.LooprOrderFillContainer
import org.loopring.looprwallet.core.models.loopr.orders.OrderFillFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_ORDER_FILLS
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
import org.loopring.looprwallet.core.utilities.RealmUtility
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.orderdetails.repositories.LooprOrderFillsRepository
import java.util.*

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An [OfflineFirstViewModel] that retrieves the details of an order (order fills)
 * by using the order's unique hash as the parameter.
 */
class OrderFillsViewModel : OfflineFirstViewModel<OrderedRealmCollection<LooprOrderFillContainer>, OrderFillFilter>() {

    override val repository = LooprOrderFillsRepository()

    override val waitTime: Long
        get() = 5 * 1000L

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    fun getOrderFills(
            owner: ViewLifecycleFragment,
            filter: OrderFillFilter,
            onChange: (LooprOrderFillContainer) -> Unit
    ) {
        initializeData(owner, filter) {
            it.firstOrNull()?.let { container ->
                container.orderFillList = repository.getOrderFills(filter)
                onChange(container)
            }
        }
    }

    override fun getLiveDataFromRepository(parameter: OrderFillFilter): LiveData<OrderedRealmCollection<LooprOrderFillContainer>> {
        return repository.getOrderFillContainer(parameter)
    }

    override fun isPredicatesEqual(oldParameter: OrderFillFilter?, newParameter: OrderFillFilter): Boolean {
        return oldParameter?.orderHash == newParameter.orderHash
    }

    override fun isRefreshNecessary(parameter: OrderFillFilter): Boolean {
        if (parameter.pageNumber != 1) {
            // We're loading a new page
            return true
        }

        return defaultIsRefreshNecessary(parameter.orderHash)
    }

    override fun getDataFromNetwork(parameter: OrderFillFilter) = async(NET) {
        val container = service.getOrderFillsByOrderHash(parameter).await()
        RealmList(container)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprOrderFillContainer>, parameter: OrderFillFilter) {
        val repository = LooprOrderFillsRepository()
        val newContainer = data.first()!!
        val oldContainer = repository.getOrderFillContainerNow(parameter, IO)

        RealmUtility.updatePagingContainer(repository, parameter.pageNumber, oldContainer, newContainer)
    }

    override fun addSyncDataToRepository(parameter: OrderFillFilter) {
        // The last sync time is based on the
        syncRepository.add(SyncData(syncType, parameter.orderHash, Date()))
    }

    override val syncType: String
        get() = SYNC_TYPE_ORDER_FILLS
}