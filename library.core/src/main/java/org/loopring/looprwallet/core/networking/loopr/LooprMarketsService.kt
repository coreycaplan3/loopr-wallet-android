package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.markets.TradingPairFilter
import org.loopring.looprwallet.core.models.markets.TradingPairTrend
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

    fun getMarketDetails(tradingPairMarket: String): Deferred<TradingPair>

    fun getMarketTrends(tradingPairFilter: TradingPairFilter): Deferred<RealmList<TradingPairTrend>>

}