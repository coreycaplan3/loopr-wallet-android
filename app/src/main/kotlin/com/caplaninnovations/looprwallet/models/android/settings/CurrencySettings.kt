package com.caplaninnovations.looprwallet.models.android.settings

import android.support.annotation.VisibleForTesting
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: This class is made open so it can be mocked.
 *
 */
open class CurrencySettings(private val looprSettings: LooprSettings) {

    companion object {
        @VisibleForTesting
        const val DEFAULT_CURRENCY = "USD"

        private const val KEY_CURRENT_CURRENCY = "_CURRENT_CURRENCY"
    }

    /**
     * @return The currency symbol that the user currently has selected for themselves
     */
    fun getCurrentCurrency(): String {
        return looprSettings.getString(KEY_CURRENT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    /**
     * @return A [DecimalFormat] that can format currency appropriately, using separators and the
     * correct symbol appropriately.
     */
    fun getCurrencyFormatter(): NumberFormat {
        return DecimalFormat.getCurrencyInstance(getCurrentLocale())
    }

    /**
     * @return A [DecimalFormat] that can format tokens and numbers appropriately, using separators
     * as appropriate.
     */
    fun getNumberFormatter(): NumberFormat {
        // Use the currency as a means to know how users format their decimal places and large
        // numbers. IE - 1,000,000.00 vs. 1.000.000,00
        return (DecimalFormat.getInstance(getCurrentLocale()) as DecimalFormat)
                .apply {
                    isDecimalSeparatorAlwaysShown = true

                    roundingMode = RoundingMode.HALF_UP

                    minimumIntegerDigits = 1
                    maximumIntegerDigits = 10

                    minimumFractionDigits = 0
                    maximumFractionDigits = 8

                    isGroupingUsed = true
                }
    }

    /**
     * @return The currency symbol in use by the user
     */
    fun getCurrencySymbol(): String {
        return getCurrencyFormatter().currency.getSymbol(getCurrentLocale())
    }

    /**
     * @return The character used by the current locale for separating numbers by decimal place.
     * IE "." or ","
     */
    fun getDecimalSeparator(): String {
        val separator = DecimalFormatSymbols.getInstance(getCurrentLocale()).decimalSeparator
        return Character.toString(separator)
    }

    /**
     * @return The [Locale] in which the user would like to view their currency information.
     */
    fun getCurrentLocale(): Locale {
        val currency = getCurrentCurrency()

        return when (currency) {
            "USD" -> Locale.US
            "GBP" -> Locale.UK
            "EUR" -> Locale.GERMAN // Default to German since they use the Euro
            "JPY" -> Locale.JAPANESE
            "KRW" -> Locale.KOREAN
            "CNY" -> Locale.CHINESE
            else -> throw IllegalArgumentException("Invalid currency, found: $currency")
        }
    }

}