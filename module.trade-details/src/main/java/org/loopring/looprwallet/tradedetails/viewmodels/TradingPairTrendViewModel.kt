package org.loopring.looprwallet.tradedetails.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.tradedetails.repositories.TradingPairTrendRepository

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairTrendViewModel : OfflineFirstViewModel<OrderedRealmCollection<TradingPairTrend>, TradingPairFilter>() {

    override val repository = TradingPairTrendRepository()

    private val service = LooprMarketsService.getInstance()

    fun getTradingPairTrends(owner: LifecycleOwner, filter: TradingPairFilter, onChange: (OrderedRealmCollection<TradingPairTrend>) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: TradingPairFilter): LiveData<OrderedRealmCollection<TradingPairTrend>> {
        return repository.getTradingPairTrend(parameter)
    }

    override fun getDataFromNetwork(parameter: TradingPairFilter): Deferred<OrderedRealmCollection<TradingPairTrend>> {
        return service.getMarketTrends(parameter.market)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<TradingPairTrend>) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: TradingPairFilter): Boolean {
        val syncId = TradingPairDetailsViewModel.getSyncId(parameter)
        return defaultIsRefreshNecessary(syncId)
    }

    override fun addSyncDataToRepository(parameter: TradingPairFilter) {
        repository.add(SyncData())
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_TRADING_PAIR_TRENDS

}