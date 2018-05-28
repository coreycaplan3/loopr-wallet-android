package org.loopring.looprwallet.core.utilities

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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

    @SuppressLint("SimpleDateFormat")
    fun formatDateTime(date: Date): String {
        val isSameYear = Calendar.getInstance()
                .also { it.time = date }
                .get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)

        val formatter = when {
            isSameYear -> SimpleDateFormat("MMM d h:MM a")
            else -> SimpleDateFormat("d MMM yyyy, h:MM a")
        }

        return formatter.format(date)
    }

}