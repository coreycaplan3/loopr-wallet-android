package org.loopring.looprwallet.homemarkets.adapters

/**
 * Created by Corey Caplan on 4/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To send [MarketFilter]
 *
 */
interface OnGeneralMarketsFilterChangeListener {

    /**
     * Called when the filter's *sort by* value changes
     *
     * @param sortByValue The new value (not UI value) that the markets sort by attribute is changed
     * to
     */
    fun onSortByChange(sortByValue: String)

    /**
     * Called when the filter's *date* value changes
     *
     * @param dateValue The new value that the markets date filter is changed to
     */
    fun onDateFilterChange(dateValue: String)

    /**
     * @return The current date filter used for filtering market information
     */
    fun getCurrentDateFilter(): String
}