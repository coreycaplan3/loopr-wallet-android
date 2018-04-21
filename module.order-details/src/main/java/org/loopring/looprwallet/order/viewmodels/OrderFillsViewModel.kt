package org.loopring.looprwallet.order.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmResults
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_ORDER_FILLS
import org.loopring.looprwallet.core.models.wallet.LooprWallet
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
class OrderFillsViewModel(currentWallet: LooprWallet) : OfflineFirstViewModel<OrderedRealmCollection<RealmModel>, String>() {

    override val repository = OrderFillsRepository(currentWallet)

    fun getOrderFills(
            owner: LifecycleOwner,
            orderId: String,
            onChange: (OrderedRealmCollection<RealmModel>) -> Unit
    ) {
        initializeData(owner, orderId, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<OrderedRealmCollection<RealmModel>> {
        return repository.getOrderDepth(parameter)
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun getDataFromNetwork(parameter: String): Deferred<OrderedRealmCollection<RealmModel>> {
        TODO("GET DATA FROM NETWORK")
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<RealmModel>) {
        repository.addList(data)
    }

    override fun addSyncDataToRepository(parameter: String) {
        // The last sync time is based on the
        syncRepository.add(SyncData(syncType, parameter, Date()))
    }

    override val syncType: String
        get() = SYNC_TYPE_ORDER_FILLS
}