package com.caplaninnovations.looprwallet.networking.prices

import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
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

    override fun getCurrentCurrencyExchangeRate(ticker: String): Deferred<CurrencyExchangeRate> = async {
        delay(500L)
        val rate = BigDecimal(Math.random()).setScale(CurrencyExchangeRate.maxExchangeRateFractionDigits, RoundingMode.HALF_UP)
        CurrencyExchangeRate(ticker, rate)
    }

}