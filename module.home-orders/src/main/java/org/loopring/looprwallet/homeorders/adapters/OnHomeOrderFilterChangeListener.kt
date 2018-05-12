package org.loopring.looprwallet.homeorders.adapters

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To pass actions uniformly from [HomeClosedOrderFilterViewHolder] and
 * [HomeOpenOrderFilterViewHolder] to the implementor.
 */
interface OnHomeOrderFilterChangeListener {

    /**
     * @return The current status filter being used
     */
    val currentStatusFilter: String
}