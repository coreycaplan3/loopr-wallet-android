package org.loopring.looprwallet.orderdetails.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
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
class OrderFillsViewModel : OfflineFirstViewModel<LooprOrderFillContainer, OrderFillFilter>() {

    override val repository = LooprOrderFillsRepository()

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    fun getOrderFills(
            owner: ViewLifecycleFragment,
            orderHash: OrderFillFilter,
            onChange: (LooprOrderFillContainer) -> Unit
    ) {
        initializeData(owner, orderHash, onChange)
    }

    override fun getLiveDataFromRepository(parameter: OrderFillFilter): LiveData<LooprOrderFillContainer> {
        return repository.getOrderFillContainer(parameter)
    }

    override fun isPredicatesEqual(oldParameter: OrderFillFilter?, newParameter: OrderFillFilter): Boolean {
        return oldParameter?.orderHash == newParameter.orderHash
    }

    override fun isRefreshNecessary(parameter: OrderFillFilter) = defaultIsRefreshNecessary(parameter.orderHash)

    override fun getDataFromNetwork(parameter: OrderFillFilter): Deferred<LooprOrderFillContainer> {
        return service.getOrderFillsByOrderHash(parameter)
    }

    override fun addNetworkDataToRepository(data: LooprOrderFillContainer, parameter: OrderFillFilter) {
        val criteria = LooprOrderFillContainer.createCriteria(parameter)
        val repository = LooprOrderFillsRepository()
        val oldContainer = repository.getOrderFillContainerByKeyNow(criteria, IO)
        RealmUtility.diffContainerAndAddToRepository(repository, oldContainer, data) { oldFill, newFill ->
            oldFill.transactionHash == newFill.transactionHash
        }
    }

    override fun addSyncDataToRepository(parameter: OrderFillFilter) {
        // The last sync time is based on the
        syncRepository.add(SyncData(syncType, parameter.orderHash, Date()))
    }

    override val syncType: String
        get() = SYNC_TYPE_ORDER_FILLS
}