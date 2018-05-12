package org.loopring.looprwallet.core.models.loopr.filters

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

object DateFilter {

    const val ONE_HOUR_MILLIS: Long = 1000 * 60 * 60
    const val TWO_HOURS_MILLIS: Long = ONE_HOUR_MILLIS * 2
    const val FOUR_HOURS_MILLIS: Long = TWO_HOURS_MILLIS * 2
    const val ONE_DAY_MILLIS: Long = ONE_HOUR_MILLIS * 24
    const val ONE_WEEK_MILLIS: Long = ONE_DAY_MILLIS * 7
    const val ONE_MONTH_MILLIS: Long = ONE_DAY_MILLIS * 30
    const val ONE_YEAR_MILLIS: Long = ONE_DAY_MILLIS * 365

    val ALL = str(R.string.all)
    val ONE_HOUR = str(R.string.all)
    val TWO_HOURS = str(R.string.all)
    val FOUR_HOURS = str(R.string.all)
    val ONE_DAY = str(R.string.all)
    val ONE_WEEK = str(R.string.all)
    val ONE_MONTH = str(R.string.all)
    val ONE_YEAR = str(R.string.all)

}