package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.networking.loopr.LooprMarketsService.Companion.SYNC_CONTEXT
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.utilities.NetworkUtility
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

        private fun getTradingPair(primaryToken: LooprToken, secondaryToken: LooprToken) = TradingPair().apply {
            this.market = "${primaryToken.ticker}-${secondaryToken.ticker}"
            this.primaryToken = primaryToken
            this.secondaryToken = secondaryToken
            this.highPrice = BigDecimal(Math.random())
            this.lowPrice = BigDecimal(Math.random())
            this.amountOfPrimary = BigDecimal(Math.random() * 1000.0)
            this.volumeOfSecondary = BigDecimal(Math.random() * 10.0)
            this.lastPrice = (highPrice + lowPrice) / BigDecimal("2.0")

            val amount = BigDecimal(Math.random()).setScale(2, RoundingMode.HALF_EVEN)
            if (Math.random() >= 0.5) {
                this.change24h = "-${amount.toPlainString()}%"
            } else {
                this.change24h = "${amount.toPlainString()}%"
            }
        }

        val appcTradingPair by lazy {
            getTradingPair(LooprToken.APPC, LooprToken.WETH)
        }

        val lrcTradingPair by lazy {
            getTradingPair(LooprToken.LRC, LooprToken.WETH)
        }

        val reqTradingPair by lazy {
            getTradingPair(LooprToken.REQ, LooprToken.WETH)
        }

        val zrxTradingPair by lazy {
            getTradingPair(LooprToken.ZRX, LooprToken.WETH)
        }

    }

    override fun getMarkets() = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async RealmList(appcTradingPair, lrcTradingPair, reqTradingPair, zrxTradingPair)
        } else {
            throw IOException("No connection")
        }
    }

    override fun syncSupportedMarkets() = async(SYNC_CONTEXT) {
        delay(3000L) // purposely make this take a long time

        if (NetworkUtility.isNetworkAvailable()) {
            val repository = BaseRealmRepository()

            val tokens = RealmList<LooprToken>(LooprToken.APPC, LooprToken.ETH, LooprToken.LRC, LooprToken.REQ, LooprToken.WETH, LooprToken.ZRX)
            repository.addList(tokens, NET)

            val tradingPairList = RealmList<TradingPair>().apply {
                add(appcTradingPair)
                add(lrcTradingPair)
                add(reqTradingPair)
                add(zrxTradingPair)
            }

            repository.addList(tradingPairList, NET)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketDetails(tradingPairMarket: String) = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async listOf(appcTradingPair, lrcTradingPair, reqTradingPair, zrxTradingPair).first { it.market == tradingPairMarket }
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketTrends(tradingPairGraphFilter: TradingPairGraphFilter) = async(NET) {
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