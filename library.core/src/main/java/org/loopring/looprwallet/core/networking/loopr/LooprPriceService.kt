package org.loopring.looprwallet.core.networking.loopr

import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprPriceQuote

/**
 * Created by corey on 5/31/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
interface LooprPriceService {

    companion object {

        fun getInstance(): LooprPriceService {
            val buildFlavor = BuildUtility.BUILD_FLAVOR
            return when (buildFlavor) {
                BuildUtility.FLAVOR_MOCKNET -> LooprPriceServiceMockImpl()
                BuildUtility.FLAVOR_TESTNET, BuildUtility.FLAVOR_MAINNET -> LooprPriceServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildFlavor")
            }
        }

    }

    fun getPriceQuote(currency: String): Deferred<LooprPriceQuote>

}