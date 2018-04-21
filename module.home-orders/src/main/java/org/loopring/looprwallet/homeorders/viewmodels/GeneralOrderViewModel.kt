package org.loopring.looprwallet.homeorders.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmResults
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.order.OrderFilter
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
class GeneralOrderViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<RealmModel>, OrderFilter>() {

    // 5 seconds in ms
    override val waitTime = 5 * 1000L

    override val repository = LooprOrderRepository(currentWallet)

    /**
     * Gets the user's orders based on the provided [filter].
     */
    fun getOrders(owner: LifecycleOwner, filter: OrderFilter, onChange: (OrderedRealmCollection<RealmModel>) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: OrderFilter): LiveData<OrderedRealmCollection<RealmModel>> {
        return repository.getOrders(parameter)
    }

    override fun isRefreshNecessary(parameter: OrderFilter) = defaultIsRefreshNecessary(parameter.address)

    override fun getDataFromNetwork(parameter: OrderFilter): Deferred<OrderedRealmCollection<RealmModel>> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<RealmModel>) {
        repository.addList(data)
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