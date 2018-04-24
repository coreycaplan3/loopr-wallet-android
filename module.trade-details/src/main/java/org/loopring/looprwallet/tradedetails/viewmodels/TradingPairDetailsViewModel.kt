package org.loopring.looprwallet.tradedetails.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.sync.SyncData
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.tradedetails.repositories.TradingPairDetailsRepository
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairDetailsViewModel : OfflineFirstViewModel<TradingPair, TradingPairFilter>() {

    companion object {

        /**
         * Gets the sync ID for the [syncRepository].
         */
        fun getSyncId(filter: TradingPairFilter): String {
            // DO NOT CHANGE - Doing so will mess up the sync ID for all backwards-compatible version
            return "${filter.market}-${filter.graphDateFilter}"
        }

    }

    override val repository = TradingPairDetailsRepository()

    /**
     * Gets the trading pair based on the provided [filter].
     */
    fun getTradingPair(owner: LifecycleOwner, filter: TradingPairFilter, onChange: (TradingPair) -> Unit) {
        return initializeData(owner, filter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: TradingPairFilter): LiveData<TradingPair> {
        return repository.getTradingPairByMarket(parameter.primaryTicker, parameter.secondaryTicker)
    }

    override fun getDataFromNetwork(parameter: TradingPairFilter): Deferred<TradingPair> {
        TODO("not implemented")
    }

    override fun addNetworkDataToRepository(data: TradingPair) {
        repository.add(data)
    }

    override fun isRefreshNecessary(parameter: TradingPairFilter): Boolean {
        // DO NOT CHANGE - Doing so will mess up the sync ID for all backwards-compatible version
        val syncId = "${parameter.market}-${parameter.graphDateFilter}"
        return defaultIsRefreshNecessary(syncId)
    }

    override fun addSyncDataToRepository(parameter: TradingPairFilter) {
        syncRepository.add(SyncData(parameter.market, parameter.graphDateFilter, Date()))
    }

    override val syncType: String
        get() = SyncData.SYNC_TYPE_TRADING_PAIR_DETAILS
}