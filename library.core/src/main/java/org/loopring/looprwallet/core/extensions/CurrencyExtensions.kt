package org.loopring.looprwallet.core.extensions

import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

object BigDecimalHelper {
    val NEGATIVE_ONE = BigDecimal(-1)
}

object BigIntegerHelper {
    val NEGATIVE_ONE = BigInteger("-1")
}

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
 * Formats a [BigInteger] that has all padding, as a token for the UI. For example, "142.25 LRC"
 */
fun BigInteger.formatAsToken(settings: CurrencySettings, token: LooprToken): String {
    return toBigDecimal(token).formatAsToken(settings, token)
}

/**
 * Formats a [BigDecimal] as a token for the UI. For example "132.29 LRC"
 */
fun BigDecimal.formatAsTokenNoTicker(settings: CurrencySettings): String {
    return settings.getNumberFormatter().format(this)
}

/**
 * Formats a [BigDecimal] as a token for the UI. For example "132.29 LRC"
 */
fun BigDecimal.formatAsToken(settings: CurrencySettings, token: LooprToken): String {
    return "${formatAsTokenNoTicker(settings)} ${token.ticker}"
}

/**
 * Formats a [BigDecimal] as the user's national currency for the UI. For example "$132.24"
 */
fun BigInteger.formatAsCurrency(settings: CurrencySettings): String {
    val formatter = settings.getCurrencyFormatter()
    val result = BigDecimal(this, 2) / BigDecimal(100)
    return formatter.format(result)
}

/**
 * Formats a [BigInteger] as a decimal (using the proper number of decimal places). For example,
 * 10000000000000000000 (equivalent of 10, with 18 decimals) becomes 10.000000000000000000
 */
fun BigInteger.toBigDecimal(token: LooprToken): BigDecimal {
    return BigDecimal(this, token.decimalPlaces) / (BigDecimal.TEN.pow(token.decimalPlaces))
}

/**
 * Converts the given [BigDecimal] to the equivalent [BigInteger] (integer), by moving the decimal
 * place over by [LooprToken.decimalPlaces] (and padding with extra zeros, where appropriate). For
 * example, 10.00 becomes 10000000000000000000 (equivalent of 10, with 18 decimals)
 */
fun BigDecimal.toBigInteger(token: LooprToken): BigInteger {
    val multiplier = BigDecimal.TEN.pow(token.decimalPlaces)
    return (this * multiplier) // this * 10^decimal
            .setScale(token.decimalPlaces, RoundingMode.HALF_DOWN)
            .toBigInteger()
}

fun BigDecimal.equalsZero(): Boolean {
    return this.unscaledValue() == BigDecimal.ZERO.unscaledValue()
}

fun BigDecimal.isIntegerValue(): Boolean {
    return signum() == 0 || scale() <= 0 || stripTrailingZeros().scale() <= 0
}
