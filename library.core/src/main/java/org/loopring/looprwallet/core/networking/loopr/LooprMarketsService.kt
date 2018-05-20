package org.loopring.looprwallet.core.networking.loopr

import android.os.Handler
import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.asCoroutineDispatcher
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairGraphFilter
import org.loopring.looprwallet.core.models.loopr.markets.TradingPairTrend
import org.loopring.looprwallet.core.utilities.BuildUtility

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

        /**
         * Context used for calling [syncSupportedMarkets]
         */
        val SYNC_CONTEXT = Handler().asCoroutineDispatcher()

        const val KEY_HAS_SYNCED_MARKETS = "HAS_SYNCED_MARKETS"

        fun getInstance(): LooprMarketsService {
            val environment = BuildUtility.BUILD_FLAVOR
            return when (environment) {
                BuildUtility.FLAVOR_MOCKNET -> LooprMarketsServiceMockImpl()
                BuildUtility.FLAVOR_TESTNET, BuildUtility.FLAVOR_MAINNET -> LooprMarketsServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid environment, found: $environment")
            }
        }

    }

    fun getMarkets(): Deferred<RealmList<TradingPair>>

    fun syncSupportedMarkets(): Deferred<Unit>

    fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair>

    fun getMarketTrends(tradingPairGraphFilter: TradingPairGraphFilter): Deferred<RealmList<TradingPairTrend>>

}