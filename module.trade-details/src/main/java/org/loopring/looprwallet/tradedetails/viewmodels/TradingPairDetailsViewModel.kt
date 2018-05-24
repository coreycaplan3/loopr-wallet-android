package org.loopring.looprwallet.tradedetails.viewmodels

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.HandlerContext
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.core.repositories.markets.TradingPairDetailsRepository
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairDetailsViewModel : OfflineFirstViewModel<TradingPair, TradingPairGraphFilter>() {

    companion object {

        /**
         * Gets the sync ID for the [syncRepository].
         */
        fun getSyncId(filter: TradingPairGraphFilter): String {
            // DO NOT CHANGE - Doing so will mess up the sync ID for all backwards-compatible version
            return "${filter.market}-${filter.graphDateFilter}"
        }

    }

    override val repository = TradingPairDetailsRepository()

    private val service by lazy {
        LooprMarketsService.getInstance()
    }

    fun doesTradingPairExist(market: String, context: HandlerContext = IO): Deferred<Boolean> {
        return repository.doesTradingPairExist(market, context)
    }

    /**
     * Gets the trading pair based on the provided [filter].
     */
    fun getTradingPair(owner: ViewLifecycleFragment, filter: TradingPairGraphFilter, onChange: (TradingPair) -> Unit) {
        initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: TradingPairGraphFilter): LiveData<TradingPair> {
        return repository.getTradingPairByMarket(parameter.market)
    }

    override fun getDataFromNetwork(parameter: TradingPairGraphFilter): Deferred<TradingPair> {
        return service.getMarketDetails(parameter.market)
    }

    override fun addNetworkDataToRepository(data: TradingPair, parameter: TradingPairGraphFilter) {
        repository.getTradingPairByMarketNow(data.market, IO)?.let { oldTradingPair ->
            data.isFavorite = oldTradingPair.isFavorite
        }
        repository.add(data)
    }

    override fun isRefreshNecessary(parameter: TradingPairGraphFilter): Boolean {
        return defaultIsRefreshNecessary(getSyncId(parameter))
    }

    override fun addSyncDataToRepository(parameter: TradingPairGraphFilter) {
        syncRepository.add(SyncData(syncType, getSyncId(parameter), Date()))
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_TRADING_PAIR_DETAILS
}