package org.loopring.looprwallet.core.networking.loopr

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.android.architecture.NET
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.NetworkUtility
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprPriceQuote
import org.loopring.looprwalletnetwork.models.loopring.responseObjects.LooprTokenPriceQuote
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by corey on 5/31/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprPriceServiceMockImpl : LooprPriceService {

    companion object {

        private fun getTokenPriceInfo(currency: String, ticker: String): LooprTokenPriceQuote {
            val price = when (ticker) {
                LooprToken.APPC.ticker -> BigInteger("5000000")
                LooprToken.ETH.ticker -> BigInteger("50000000")
                LooprToken.LRC.ticker -> BigInteger("1000000")
                LooprToken.REQ.ticker -> BigInteger("9000000")
                LooprToken.WETH.ticker -> BigInteger("50000000")
                LooprToken.ZRX.ticker -> BigInteger("3000000")
                else -> throw IllegalArgumentException("Invalid token ticker, found: $ticker")
            }

            return when (currency) {
                CurrencySettings.USD -> LooprTokenPriceQuote().apply {
                    this.symbol = ticker
                    this.price = price.toBigDecimal() / BigDecimal(10000)
                }
                CurrencySettings.CNY -> LooprTokenPriceQuote().apply {
                    this.symbol = ticker
                    this.price = price.toBigDecimal() / BigDecimal(10000) * BigDecimal(6.41)
                }
                else -> throw IllegalArgumentException("Invalid currency, found: $currency")
            }

        }


    }

    override fun getPriceQuote(currency: String): Deferred<LooprPriceQuote> = async(NET) {
        delay(NetworkUtility.MOCK_SERVICE_CALL_DURATION)

        if (NetworkUtility.isNetworkAvailable()) {
            LooprPriceQuote().apply {
                tokens = RealmList(
                        getTokenPriceInfo(currency, LooprToken.APPC.ticker),
                        getTokenPriceInfo(currency, LooprToken.ETH.ticker),
                        getTokenPriceInfo(currency, LooprToken.LRC.ticker),
                        getTokenPriceInfo(currency, LooprToken.REQ.ticker),
                        getTokenPriceInfo(currency, LooprToken.WETH.ticker),
                        getTokenPriceInfo(currency, LooprToken.ZRX.ticker)
                )
            }
        } else {
            throw IOException("No connection")
        }
    }
}