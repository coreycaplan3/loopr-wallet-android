package org.loopring.looprwallet.core.utilities

import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object DateUtility {

    /**
     * Checks if the provided [date] (in UTC) plus an offset is after the current time (in UTC)
     *
     * @param date The date in UTC time that will be checked against the current time
     * @param offsetMillis The offset from [date] that will be measured against the current time
     */
    fun isTimeAfterCurrent(date: Date, offsetMillis: Long): Boolean {
        val current = Date()

        return date.time + offsetMillis > current.time
    }

}