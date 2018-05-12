package org.loopring.looprwallet.core.extensions

/**
 * Created by corey on 5/11/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */

/**
 * Counts the number of digits before the decimal point. If there's no decimal point, it
 * counts all the digits.
 *
 * @return The number of characters before the decimal point or the length of the string if there
 * was no decimal point found
 */
fun String.getAmountBeforeDecimal(): Int {
    this.forEachIndexed { i, ch ->
        if (ch == '.') return i
    }
    return this.length
}

/**
 * Counts the number of digits after the decimal point. If there's no decimal point, 0 is
 * returned.
 */
fun String.getAmountAfterDecimal(): Int {
    var decimalFlag = false
    var counter = 0
    this.forEach { ch ->
        if (decimalFlag) counter += 1
        if (ch == '.') decimalFlag = true
    }
    return counter
}