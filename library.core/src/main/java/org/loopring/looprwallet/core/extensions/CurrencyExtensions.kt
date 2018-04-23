package org.loopring.looprwallet.core.extensions

import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigDecimal
import java.math.BigInteger
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
 * Formats a [BigDecimal] as a token
 */
fun BigInteger.formatAsToken(settings: CurrencySettings, token: LooprToken): String {
    val formatter = settings.getNumberFormatter()
    val result =  BigDecimal(this, token.decimalPlaces) / (BigDecimal.TEN.pow(token.decimalPlaces))
    return "${formatter.format(result)} ${token.ticker}"
}

/**
 * Formats a [BigDecimal] as the user's national currency
 */
fun BigInteger.formatAsCurrency(settings: CurrencySettings): String {
    val formatter = settings.getCurrencyFormatter()
    val result = BigDecimal(this, 2) / BigDecimal(100)
    return formatter.format(result)
}

fun BigDecimal.equalsZero(): Boolean {
    return this.unscaledValue() == BigDecimal.ZERO.unscaledValue()
}

fun BigDecimal.isIntegerValue(): Boolean {
    return signum() == 0 || scale() <= 0 || stripTrailingZeros().scale() <= 0
}
