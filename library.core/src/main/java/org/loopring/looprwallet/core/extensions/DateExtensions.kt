package org.loopring.looprwallet.core.extensions

import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * Converts this date object from UTC to the native time on the device.
 *
 * @param otherTimeZone The other time zone that will be computed against UTC
 * @return A new date object whose time is set to the native one on the device
 */
fun Date.fromUtcToOther(otherTimeZone: TimeZone = TimeZone.getDefault()): Date {

    // First check if we're in day light savings time (depending on time of year)
    val dayLightSavingsOffset = when {
        otherTimeZone.inDaylightTime(this) -> otherTimeZone.dstSavings
        else -> 0
    }

    val offset = otherTimeZone.rawOffset + dayLightSavingsOffset

    // Native = UTC + offset
    return Date(this.time + offset)
}

/**
 * Converts this date object from  the native time on the device to UTC.
 *
 * @param otherTimeZone The other time zone that will be computed with UTC
 * @return A new date object whose time is set to UTC.
 */
fun Date.fromOtherToUtc(otherTimeZone: TimeZone = TimeZone.getDefault()): Date {

    // First check if we're in day light savings time (depending on time of year)
    val rawUtcDate = Date(this.time - otherTimeZone.rawOffset)
    val dayLightSavingsOffset = when {
        otherTimeZone.inDaylightTime(rawUtcDate) -> otherTimeZone.dstSavings
        else -> 0
    }

    // UTC = Native - Offset
    return Date(rawUtcDate.time - dayLightSavingsOffset)
}
