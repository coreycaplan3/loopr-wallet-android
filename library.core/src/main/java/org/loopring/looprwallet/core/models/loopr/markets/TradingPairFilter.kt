package org.loopring.looprwallet.core.models.loopr.markets

import android.util.Property
import io.realm.Sort
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ApplicationUtility.strArray
import kotlin.reflect.KProperty

/**
 * Created by Corey Caplan on 4/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide sorting criteria for the markets page
 *
 * @property isFavorites True if the filter should find *favorites* tickers or false otherwise
 * @property sortBy The criteria on which to sort the markets page
 * @property changePeriod The period over which the market changes are calculated. For example,
 * percentage change of a coin's value over 24 hours vs 1 hour.
 *
 * @see SORT_BY_ARRAY_UI
 * @see SORT_BY_ARRAY_VALUES
 * @see CHANGE_PERIOD_1D
 */
data class TradingPairFilter(val isFavorites: Boolean, val changePeriod: String) {

    companion object {

        val SORT_BY_ARRAY_UI: Array<String> = strArray(R.array.sort_markets_ui)
        val SORT_BY_ARRAY_VALUES: Array<String> = strArray(R.array.sort_markets_values)

        val SORT_BY_TICKER_ASC: String = str(R.string.sort_ticker_asc)
        val SORT_BY_GAINERS: String = str(R.string.sort_percentage_change_asc)
        val SORT_BY_LOSERS: String = str(R.string.sort_percentage_change_desc)

        /**
         * Change period over 1 day
         */
        val CHANGE_PERIOD_1D: String = str(R.string._1d)

        /**
         * Given a sort by value enum, convert it to its respective UI (human-readable) counterpart.
         */
        fun convertValueToUi(sortByValue: String): String {
            return SORT_BY_ARRAY_UI[SORT_BY_ARRAY_VALUES.indexOf(sortByValue)]
        }

        /**
         * Given a sort by value enum, convert it to its respective UI (human-readable) counterpart.
         */
        fun convertValueToProperty(sortByValue: String): Pair<KProperty<*>, Sort> = when (sortByValue) {
            SORT_BY_TICKER_ASC -> TradingPair::market to Sort.ASCENDING
            SORT_BY_TICKER_ASC -> TradingPair::change24h to Sort.ASCENDING
            SORT_BY_TICKER_ASC -> TradingPair::change24h to Sort.DESCENDING
            else -> throw IllegalArgumentException("Invalid sortByValue, found: $sortByValue")
        }

    }

}