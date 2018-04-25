package org.loopring.looprwallet.order.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.order.LooprOrderFill
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_ORDER_FILLS
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.networking.loopr.LooprOrderService
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.order.repositories.OrderFillsRepository
import java.util.*

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An [OfflineFirstViewModel] that retrieves the details of an order (order fills)
 * by using the order's unique hash as the parameter.
 */
class OrderFillsViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<LooprOrderFill>, String>() {

    override val repository = OrderFillsRepository(currentWallet)

    private val service by lazy {
        LooprOrderService.getInstance()
    }

    fun getOrderFills(
            owner: BaseFragment,
            orderHash: String,
            onChange: (OrderedRealmCollection<LooprOrderFill>) -> Unit
    ) {
        initializeData(owner, orderHash, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<LooprOrderFill>> {
        return repository.getOrderDepth(parameter)
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun getDataFromNetwork(parameter: String): Deferred<OrderedRealmCollection<LooprOrderFill>> {
        return service.getOrderFillsByOrderHash(parameter)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<LooprOrderFill>) {
        repository.addList(data)
    }

    override fun addSyncDataToRepository(parameter: String) {
        // The last sync time is based on the
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    override val syncType: String
        get() = SYNC_TYPE_ORDER_FILLS
}