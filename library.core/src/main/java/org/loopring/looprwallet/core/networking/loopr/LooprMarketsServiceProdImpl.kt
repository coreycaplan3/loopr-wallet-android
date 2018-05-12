package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend

/**
 * Created by corey on 5/9/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprMarketsServiceProdImpl: LooprMarketsService {

    private val service by lazy {

    }

    override fun getMarkets(): Deferred<RealmList<TradingPair>> {
        TODO("...")
    }

    override fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMarketTrends(tradingPairGraphFilter: TradingPairGraphFilter): Deferred<RealmList<TradingPairTrend>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}