package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend
import org.loopring.looprwallet.core.utilities.NetworkUtility
import java.io.IOException
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

        val lrcTradingPair = TradingPair(
                primaryTicker = "LRC",
                secondaryTicker = "WETH",
                isFavorite = true,
                lastPrice = 0.85,
                change24h = "20.25%",
                highPrice = 0.88,
                lowPrice = 0.80,
                amountOfPrimary = 19430240.0,
                volumeOfSecondary = 194302.0,
                primaryToken = LooprToken.LRC
        )

        val reqTradingPair = TradingPair(
                primaryTicker = "REQ",
                secondaryTicker = "WETH",
                isFavorite = true,
                lastPrice = 0.25,
                change24h = "10.54%",
                highPrice = 0.27,
                lowPrice = 0.24,
                amountOfPrimary = 38030240.0,
                volumeOfSecondary = 380302.0,
                primaryToken = LooprToken.REQ
        )

        val zrxTradingPair = TradingPair(
                primaryTicker = "ZRX",
                secondaryTicker = "WETH",
                isFavorite = true,
                lastPrice = 0.43,
                change24h = "-68.25%",
                highPrice = 0.44,
                lowPrice = 0.40,
                amountOfPrimary = 20030240.0,
                volumeOfSecondary = 200302.0,
                primaryToken = LooprToken.ZRX
        )

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

    override fun getMarketTrends(tradingPairFilter: TradingPairFilter): Deferred<RealmList<TradingPairTrend>> = async(NET) {
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
                list.add(TradingPairTrend(tradingPairFilter.market, tradingPairFilter.graphDateFilter, high, low, startDate, endDate))
            }

            return@async list
        } else {
            throw IOException("No connection")
        }
    }

}