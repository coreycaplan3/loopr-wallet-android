package org.loopring.looprwallet.core.extensions

import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigDecimal
import java.text.NumberFormat

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * @return A string that is formatted for custom currency input, as seen in the
 * *CreateTransferAmountFragment* class.
 */
fun String.formatAsCustomCurrency(currencySettings: CurrencySettings): String {
    val decimalIndex = this.indexOf('.')
    val isDecimal = decimalIndex != -1
    val integerFormat = NumberFormat.getIntegerInstance(currencySettings.getCurrentLocale())

    val wholeNumberFormatted = when {
        isDecimal -> integerFormat.format(BigDecimal(this.substring(0, decimalIndex)))
        else -> integerFormat.format(BigDecimal(this))
    }

    val entireNumber = when {
        isDecimal ->
            // We only append the numbers that are ACTUALLY there, meaning there's no padded 0's
            wholeNumberFormatted + this.substring(decimalIndex, length)
        else -> wholeNumberFormatted
    }

    return "${currencySettings.getCurrencySymbol()}$entireNumber"
}

/**
 * @return A string that is formatted for custom token input, as seen in the
 * *CreateTransferAmountFragment* class.
 */
fun String.formatAsCustomToken(currencySettings: CurrencySettings, ticker: String): String {
    val decimalIndex = this.indexOf('.')
    val isDecimal = decimalIndex != -1
    val integerFormat = NumberFormat.getIntegerInstance(currencySettings.getCurrentLocale())

    val integersFormatted = when {
        isDecimal -> integerFormat.format(BigDecimal(this.substring(0, decimalIndex)))
        else -> integerFormat.format(BigDecimal(this))
    }

    val entireNumber = when {
        isDecimal ->
            // We only append the numbers that are ACTUALLY there, meaning there's no padded 0's
            integersFormatted + this.substring(decimalIndex, length)
        else -> integersFormatted
    }

    return "$entireNumber $ticker"
}

val NEGATIVE_ONE = BigDecimal(-1)

/**
 * Formats a [BigDecimal] in the user's native currency
 */
fun BigDecimal.formatAsCurrency(settings: CurrencySettings): String {
    val value = settings.getCurrencyFormatter().format(this)
    return when {
        this.isIntegerValue() -> value.substring(0, value.length - 3)
        value.last() == '0' -> value
        else -> value
    }
}

/**
 * Formats a [BigDecimal] in the user's native currency
 */
fun BigDecimal.formatAsToken(settings: CurrencySettings, tokenTicker: String): String {
    val value = settings.getNumberFormatter().format(this)
    return when {
        this.equalsZero() -> {
            "${value}00 $tokenTicker"
        }
        this.isIntegerValue() -> {
            "${value}00 $tokenTicker"
        }
        else -> {
            "$value $tokenTicker"
        }
    }
}

fun BigDecimal.equalsZero(): Boolean {
    return this.unscaledValue() == BigDecimal.ZERO.unscaledValue()
}

fun BigDecimal.isIntegerValue(): Boolean {
    return signum() == 0 || scale() <= 0 || stripTrailingZeros().scale() <= 0
}