package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.markets.TradingPair
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
                "LRC",
                "WETH",
                true,
                0.85,
                "20.25%",
                0.88,
                0.80,
                19430240.0,
                194302.0
        )

        val reqTradingPair = TradingPair(
                "REQ",
                "WETH",
                true,
                0.25,
                "10.54%",
                0.27,
                0.24,
                38030240.0,
                380302.0
        )

        val zrxTradingPair = TradingPair(
                "ZRX",
                "WETH",
                true,
                0.85,
                "-10.25%",
                0.88,
                0.80,
                20030240.0,
                200302.0
        )

    }

    override fun getMarkets(): Deferred<RealmList<TradingPair>> = async(CommonPool) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            RealmList(lrcTradingPair, reqTradingPair, zrxTradingPair)
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair> = async(CommonPool) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            return@async lrcTradingPair
        } else {
            throw IOException("No connection")
        }
    }

    override fun getMarketTrends(tradingPairMarket: String): Deferred<RealmList<TradingPairTrend>> = async(CommonPool) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {

            val list = RealmList<TradingPairTrend>()
            for (i in 0..25) {
                val offset = (25 - i) * 60 * 2000L
                val time = Date().time - offset
                val startDate = Date(time)
                val endDate = Date(time + 500L)
                list.add(TradingPairTrend("LRC-WETH", "1H", 1.00, 0.84, startDate, endDate))
            }

            return@async list
        } else {
            throw IOException("No connection")
        }
    }

}