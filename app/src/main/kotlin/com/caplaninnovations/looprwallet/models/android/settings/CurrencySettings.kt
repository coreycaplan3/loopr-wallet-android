package com.caplaninnovations.looprwallet.models.android.settings

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CurrencySettings(private val looprSettings: LooprSettings) {

    companion object {
        private const val DEFAULT_CURRENCY = "USD"

        private const val KEY_CURRENT_CURRENCY = "_CURRENT_CURRENCY"
    }

    fun getCurrentCurrency(): String {
        return looprSettings.getString(KEY_CURRENT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun setCurrentCurrency(currency: String) {
        looprSettings.putString(KEY_CURRENT_CURRENCY, currency)
    }

    fun getCurrencyInstance(): NumberFormat {
        return DecimalFormat.getCurrencyInstance(getCurrentLocale())
    }

    fun getTokenInstance(): NumberFormat {
        // Use the currency as a means to know how users format their decimal places and large
        // numbers. IE - 1,000,000.00 vs. 1.000.000,00
        return DecimalFormat.getInstance(getCurrentLocale())
                .apply {
                    maximumIntegerDigits = 10
                    maximumFractionDigits = 8
                }
    }

    /**
     * @return The character used by the current locale for separating numbers by decimal place.
     * IE "." or ","
     */
    fun getDecimalSeparator(): String {
        val separator = DecimalFormatSymbols.getInstance(getCurrentLocale()).decimalSeparator
        return Character.toString(separator)
    }

    private fun getCurrentLocale(): Locale {
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