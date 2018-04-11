package org.loopring.looprwallet.core.models.loopr

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To store the possible predicates for filtering orders in a POKO (Kotlin POJO)
 */
data class OrderFilter(val address: String, val dateFilter: String, val statusFilter: String) {

    companion object {
        val FILTER_OPEN_ALL: String = str(R.string.filter_order_open)
        val FILTER_FILLED: String = str(R.string.filter_order_filled)
        val FILTER_CANCELLED: String = str(R.string.filter_order_cancelled)
    }

}