package org.loopring.looprwallet.homeorders.adapters

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To pass actions uniformly from [GeneralClosedOrderFilterViewHolder] and
 * [GeneralOpenOrderFilterViewHolder] to the implementor.
 */
interface OnGeneralOrderFilterChangeListener {

    /**
     * Called when the filter's status changes (EX: all vs. partial orders)
     *
     * @param newStatusValue The newly selected status enum value
     */
    fun onStatusFilterChange(newStatusValue: String)

    /**
     * Called when the filter's date changes (EX: all vs. orders in past 7 days)
     */
    fun onDateFilterChange(newDateValue: String)
}