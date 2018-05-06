package org.loopring.looprwallet.tradedetails.view

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter


/**
 * Created by corey on 5/6/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class DateXAxisValueFormatter(private val chart: BarLineChartBase<*>) : IAxisValueFormatter {

    private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {

        val days = value.toInt()
        val year = determineYear(days)
        val month = determineMonth(days)
        val monthName = months[month % months.size]
        val yearName = year.toString()

        return when {
            chart.visibleXRange > 30 * 6 -> "$monthName $yearName"
            else -> {

                val dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016))

                val appendix = when (dayOfMonth) {
                    1 -> "st"
                    2 -> "nd"
                    3 -> "rd"
                    21 -> "st"
                    22 -> "nd"
                    23 -> "rd"
                    31 -> "st"
                    else -> "th"
                }

                when (dayOfMonth) {
                    0 -> ""
                    else -> dayOfMonth.toString() + appendix + " " + monthName
                }
            }
        }
    }

    // MARK - Private Methods

    private fun getDaysForMonth(month: Int, year: Int): Int {

        // month is 0-based

        if (month == 1) {
            val is29Feb = when {
                year < 1582 -> (if (year < 1) year + 1 else year) % 4 == 0
                year > 1582 -> year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
                else -> false
            }

            return when {
                is29Feb -> 29
                else -> 28
            }
        }

        return when (month) {
            3, 5, 8, 10 -> 30
            else -> 31
        }
    }

    private fun determineMonth(dayOfYear: Int): Int {
        var month = -1
        var days = 0

        while (days < dayOfYear) {
            month += 1

            if (month >= 12)
                month = 0

            val year = determineYear(days)
            days += getDaysForMonth(month, year)
        }

        return Math.max(month, 0)
    }

    private fun determineDayOfMonth(days: Int, month: Int): Int {
        var count = 0
        var daysForMonths = 0

        while (count < month) {
            val year = determineYear(daysForMonths)
            daysForMonths += getDaysForMonth(count % 12, year)
            count++
        }

        return days - daysForMonths
    }

    private fun determineYear(days: Int): Int {
        return when {
            days <= 366 -> 2016
            days <= 730 -> 2017
            days <= 1094 -> 2018
            days <= 1458 -> 2019
            else -> 2020
        }

    }

}