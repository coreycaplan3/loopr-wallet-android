package org.loopring.looprwallet.core.models.markets

/**
 * Created by Corey Caplan on 4/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To provide sorting criteria for the markets page
 *
 * @property sortBy The criteria on which to sort the markets page
 * @property changePeriod The period over which the market changes are calculated. For example,
 * percentage change of a coin's value over 24 hours vs 1 hour.
 *
 * @see SORT_BY_VALUES
 */
data class MarketsFilter(val sortBy: String, val changePeriod: String) {

    companion object {


        val SORT_BY_UI = arrayOf<String>()
        val SORT_BY_VALUES = arrayOf<String>()

        /**
         * Given a sort by value enum, convert it to its respective UI (human-readable) counterpart.
         */
        fun convertValueToUi(sortByValue: String): String {
            TODO("IMPLEMENT")
        }
    }

}