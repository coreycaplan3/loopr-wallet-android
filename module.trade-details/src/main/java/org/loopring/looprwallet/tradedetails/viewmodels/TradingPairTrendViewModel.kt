package org.loopring.looprwallet.tradedetails.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
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
class TradingPairTrendViewModel : OfflineFirstViewModel<OrderedRealmCollection<TradingPairTrend>, TradingPairGraphFilter>() {

    override val repository = TradingPairTrendRepository()

    private val service = LooprMarketsService.getInstance()

    fun getTradingPairTrends(owner: ViewLifecycleFragment, filter: TradingPairGraphFilter, onChange: (OrderedRealmCollection<TradingPairTrend>) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: TradingPairGraphFilter): LiveData<OrderedRealmCollection<TradingPairTrend>> {
        return repository.getTradingPairTrend(parameter)
    }

    override fun getDataFromNetwork(parameter: TradingPairGraphFilter): Deferred<OrderedRealmCollection<TradingPairTrend>> {
        return service.getMarketTrends(parameter)
    }

    override fun addNetworkDataToRepository(data: OrderedRealmCollection<TradingPairTrend>, parameter: TradingPairGraphFilter) {
        repository.addList(data)
    }

    override fun isRefreshNecessary(parameter: TradingPairGraphFilter): Boolean {
        val syncId = TradingPairDetailsViewModel.getSyncId(parameter)
        return defaultIsRefreshNecessary(syncId)
    }

    override fun addSyncDataToRepository(parameter: TradingPairGraphFilter) {
        syncRepository.add(SyncData(parameter.market))
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_TRADING_PAIR_TRENDS

}