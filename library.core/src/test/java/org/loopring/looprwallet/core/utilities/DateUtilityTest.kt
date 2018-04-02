package org.loopring.looprwallet.core.utilities

import org.loopring.looprwallet.core.extensions.fromOtherToUtc
import org.loopring.looprwallet.core.extensions.fromUtcToOther
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Created by Corey Caplan on 3/15/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class DateUtilityTest {

    /**
     * We will use Eastern Time for our example
     */
    private val timeZone = TimeZone.getTimeZone("ET")

    private val FOUR_HOURS_IN_SECONDS = 60 * 60 * 4
    private val FIVE_HOURS_IN_SECONDS = 60 * 60 * 5

    /**
     * A time that does **NOT** have light savings mode on. Meaning, time has sprung forward by an
     * hour.
     */
    private val regularTime: Long by lazy {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        calendar.set(Calendar.YEAR, 2018)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.timeInMillis
    }

    /**
     * A time that has day light savings mode on. Meaning, time has fallen back by an hour
     */
    private val dayLightSavingsTime: Long by lazy {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        calendar.set(Calendar.YEAR, 2018)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.timeInMillis
    }

    @Test
    fun date_fromUtcToOther_noDst() {
        // Get time using the default time zone
        val utcTime = Date(regularTime).time / 1000L // Truncate the millisecond

        val otherTime = Date(regularTime).fromUtcToOther(timeZone).time / 1000L // Truncate the millisecond

        // OTHER = UTC - offset
        assertEquals(otherTime, utcTime - FOUR_HOURS_IN_SECONDS)
    }

    @Test
    fun date_fromUtcToOther_dst() {
        // Get time using the default time zone
        val utcTime = Date(dayLightSavingsTime).time / 1000L // Truncate the millisecond

        val otherTime = Date(dayLightSavingsTime).fromUtcToOther(timeZone).time / 1000L // Truncate the millisecond

        // OTHER = UTC - offset
        assertEquals(otherTime, utcTime - FIVE_HOURS_IN_SECONDS)
    }


    @Test
    fun date_fromOtherToUtc_noDst() {
        // Get time using the default time zone
        val utcTime = Date(regularTime).time / 1000L // Truncate the millisecond

        val otherTime = Date(regularTime).fromOtherToUtc(timeZone).time / 1000L // Truncate the millisecond

        // OTHER = UTC - offset
        assertEquals(otherTime, utcTime - FOUR_HOURS_IN_SECONDS)
    }

    @Test
    fun date_fromOtherToUtc_dst() {
        // Get time using the default time zone
        val utcTime = Date(dayLightSavingsTime).time / 1000L // Truncate the millisecond

        val otherTime = Date(dayLightSavingsTime).fromOtherToUtc(timeZone).time / 1000L // Truncate the millisecond

        // OTHER = UTC - offset
        assertEquals(otherTime, utcTime - FIVE_HOURS_IN_SECONDS)
    }

    @Test
    fun isSuppliedTimeAfterCurrent() {
        val supplier = Date()
        val offset = 1000 * 10L // 10 seconds

        assertTrue(DateUtility.isTimeAfterCurrent(supplier, offset))
        assertFalse(DateUtility.isTimeAfterCurrent(supplier, -offset))
    }

}