package com.caplaninnovations.looprwallet.networking.prices

import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.utilities.NetworkUtility.MOCK_SERVICE_CALL_DURATION
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CurrencyExchangeServiceMockImpl : CurrencyExchangeService {

    override fun getCurrentCurrencyExchangeRate(currency: String): Deferred<CurrencyExchangeRate> = async {

        delay(MOCK_SERVICE_CALL_DURATION)

        val rate = if (currency != CurrencyExchangeRate.USD.currency) {
            BigDecimal(Math.random())
                    .setScale(CurrencyExchangeRate.MAX_EXCHANGE_RATE_FRACTION_DIGITS, RoundingMode.HALF_UP)
        } else {
            CurrencyExchangeRate.USD.rateAgainstToUsd
        }
        CurrencyExchangeRate(currency, rate)
    }

}