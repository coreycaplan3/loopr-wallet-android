package org.loopring.looprwallet.homemarkets.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.models.sync.SyncData.Companion.SYNC_TYPE_MARKETS
import org.loopring.looprwallet.core.repositories.loopr.LooprMarketsRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import java.util.*

/**
 * Created by Corey Caplan on 4/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeMarketsViewModel : OfflineFirstViewModel<OrderedRealmCollection<TradingPair>, MarketsFilter>() {

    override val repository = LooprMarketsRepository()

    fun getHomeMarkets(
            owner: LifecycleOwner,
            filter: MarketsFilter,
            onChange: (OrderedRealmCollection<TradingPair>) -> Unit
    ) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: MarketsFilter): LiveData<OrderedRealmCollection<TradingPair>> {
        return repository.getMarkets(parameter)
    }

    override fun getDataFromNetwork(parameter: MarketsFilter): Deferred<OrderedRealmCollection<TradingPair>> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<TradingPair>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: MarketsFilter) = defaultIsRefreshNecessary()

    override fun addSyncDataToRepository(parameter: MarketsFilter) {
        repository.add(SyncData(syncType, null, Date()))
    }

    override val syncType = SYNC_TYPE_MARKETS
}