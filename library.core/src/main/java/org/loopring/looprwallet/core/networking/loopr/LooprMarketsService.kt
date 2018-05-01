package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LooprMarketsService {

    companion object {

        fun getInstance(): LooprMarketsService {
            // TODO
            return LooprMarketsServiceMockImpl()
        }

    }

    fun getMarkets(): Deferred<RealmList<TradingPair>>

    fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair>

    fun getMarketTrends(tradingPairFilter: TradingPairFilter): Deferred<RealmList<TradingPairTrend>>

}