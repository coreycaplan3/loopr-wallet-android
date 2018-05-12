package org.loopring.looprwallet.core.models.loopr.markets

import io.realm.RealmObject
import java.util.*

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: The trend data for a given [tradingPair], based on the time interval
 * ([graphDateFilter])
 *
 * @property tradingPair The trading pair, formatted as *primaryTicker-secondaryTicker*
 * @property graphDateFilter The interval over which the trading pair trend was measured
 * @property high The high over the given [cycleDate]
 * @property low The low over the given [cycleDate]
 * @property startDate The time at which the statistical cycle started
 * @property endDate The time at which the statistical cycle ended
 */
open class TradingPairTrend(
        var tradingPair: String = "",
        var graphDateFilter: String = TradingPairGraphFilter.GRAPH_DATE_FILTER_1H,
        var high: Double = 0.00,
        var low: Double = 0.00,
        var startDate: Date = Date(),
        var endDate: Date = Date()
) : RealmObject() {

    /**
     * The average of [high] and [low].
     */
    val averagePrice: Double
        get() = (high + low) / 2.0

    /**
     * The average of [startDate] and [endDate]
     */
    val cycleDate: Date
        get() {
            val averageTime = (endDate.time + startDate.time) / 2L
            return Date(averageTime)
        }
}