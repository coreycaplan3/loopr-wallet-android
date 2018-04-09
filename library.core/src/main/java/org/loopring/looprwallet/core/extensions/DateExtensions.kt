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
 * **NOTE: THE TWO DATE OBJECTS MUST BE IN UTC (UNADJUSTED FOR TIME ZONES) FOR THIS TO WORK**
 *
 * @param other The other date to be compared with this one, to see if they fall on the same day
 * @return true if the two dates fall on the same day or false otherwise
 */
fun Date.isSameDay(other: Date): Boolean {
    val c1 = Calendar.getInstance(TimeZone.getDefault()).apply { time = this@isSameDay }
    val c2 = Calendar.getInstance(TimeZone.getDefault()).apply { time = other }

    return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) &&
            c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
}

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
