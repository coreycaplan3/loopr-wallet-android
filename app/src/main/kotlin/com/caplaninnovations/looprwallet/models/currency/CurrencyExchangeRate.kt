package com.caplaninnovations.looprwallet.models.currency

import com.caplaninnovations.looprwallet.models.TrackedRealmObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To represent the price difference in fiat currencies from USD to others.
 *
 * @param currency The currency, represented by ticker
 * @param rateAgainstToUsd The rate at which the given currency is being traded against the USD
 * @param lastUpdated The UTC time at which this currency's rate was last updated
 */
open class CurrencyExchangeRate(
        @PrimaryKey var currency: String = "USD",
        rateAgainstToUsd: BigDecimal = BigDecimal("1.000000"),
        override var lastUpdated: Date = Date()
) : RealmObject(), TrackedRealmObject {

    companion object {
        const val maxWholeDigits = 10
        const val maxExchangeRateFractionDigits = 8
        const val maxCurrencyFractionDigits = 2

        val USD = CurrencyExchangeRate("USD", BigDecimal("1.00000000"))
    }

    /**
     * A non-backing field for wrapping around [mRateComparedToUsd]
     */
    var rateAgainstToUsd: BigDecimal
        get() {
            return BigDecimal(mRateComparedToUsd).setScale(maxExchangeRateFractionDigits, RoundingMode.HALF_UP)
        }
        set(value) {
            if (value.scale() != maxExchangeRateFractionDigits) {
                throw IllegalArgumentException("Scale should be set to $maxExchangeRateFractionDigits, found ${value.scale()}")
            }
            mRateComparedToUsd = value.toString()
        }

    private var mRateComparedToUsd: String = rateAgainstToUsd.toString()

}