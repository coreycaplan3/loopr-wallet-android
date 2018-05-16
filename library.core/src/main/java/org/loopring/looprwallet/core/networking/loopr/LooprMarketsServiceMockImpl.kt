package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprTicker
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprMarketsServiceMockImpl : LooprMarketsService {

    companion object {

        private fun getRandomLooprTicker(market: String) = LooprTicker().apply {
            this.market = market
            this.buy = BigDecimal(Math.random())
            this.sell = BigDecimal(Math.random())
            this.vol = BigDecimal(Math.random() * 1000.0)
            this.amount = BigDecimal(Math.random() * 10.0)

            val amount = BigDecimal(Math.random()).setScale(2, RoundingMode.HALF_EVEN)
            if (Math.random() >= 0.5) {
                this.change = "-${amount.toPlainString()}%"
            } else {
                this.change = "${amount.toPlainString()}%"
            }
        }

        val lrcTradingPair = TradingPair("LRC-WETH", getRandomLooprTicker("LRC-WETH"), LooprToken.LRC, LooprToken.WETH)

        val reqTradingPair = TradingPair("REQ-WETH", getRandomLooprTicker("REQ-WETH"), LooprToken.REQ, LooprToken.WETH)

        val zrxTradingPair = TradingPair("ZRX-WETH", getRandomLooprTicker("ZRX-WETH"), LooprToken.ZRX, LooprToken.WETH)

    }

    override fun getMarkets(): Deferred<RealmList<TradingPair>> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(lrcTradingPair, reqTradingPair, zrxTradingPair)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async listOf(lrcTradingPair, reqTradingPair, zrxTradingPair).first { it.market == tradingPairMarket }
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketTrends(tradingPairGraphFilter: TradingPairGraphFilter): Deferred<RealmList<TradingPairTrend>> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {

            val list = RealmList<TradingPairTrend>()
            for (i in 0..25) {
                val offset = (25 - i) * 60 * 2000L
                val base = Math.random() * 100
                val high = base + 25
                val low = base - 10
                val time = Date().time - offset
                val startDate = Date(time)
                val endDate = Date(time + 500L)
                list.add(TradingPairTrend(tradingPairGraphFilter.market, tradingPairGraphFilter.graphDateFilter, high, low, startDate, endDate))
            }

            return@async list
        } else {
            throw IOException("No connection")
        }
    }

}