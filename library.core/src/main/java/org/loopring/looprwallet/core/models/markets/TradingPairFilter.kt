package org.loopring.looprwallet.core.models.markets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: To provide a filter mechanism for getting trading pair information
 *
 * @param primaryTicker The first ticker in a trading pair. For example, the primary ticker in
 * LRC-WETH is LRC.
 * @param secondaryTicker The second ticker in a trading pair. For example, the secondary ticker in
 * LRC-WETH is WETH.
 * @param graphDateFilter The date filter to apply for filtering the graph data.
 * @see GRAPH_DATE_FILTER_1H
 * @see GRAPH_DATE_FILTER_2H
 * @see GRAPH_DATE_FILTER_4H
 * @see GRAPH_DATE_FILTER_1D
 * @see GRAPH_DATE_FILTER_1W
 */
@Parcelize
data class TradingPairFilter(
        val primaryTicker: String,
        val secondaryTicker: String,
        val graphDateFilter: String
) : Parcelable {

    val market: String
        get() = "$primaryTicker-$secondaryTicker"

    companion object {

        val GRAPH_DATE_FILTER_1H = str(R.string._1h)
        val GRAPH_DATE_FILTER_2H = str(R.string._2h)
        val GRAPH_DATE_FILTER_4H = str(R.string._4h)
        val GRAPH_DATE_FILTER_1D = str(R.string._1d)
        val GRAPH_DATE_FILTER_1W = str(R.string._1w)

    }

}