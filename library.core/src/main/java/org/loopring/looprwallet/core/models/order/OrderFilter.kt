package org.loopring.looprwallet.core.models.order

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ApplicationUtility.strArray

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To store the possible predicates for filtering orders in a POKO (Kotlin POJO)
 */
data class OrderFilter(
        val address: String,
        val ticker: String?,
        val dateFilter: String,
        val statusFilter: String
) {

    companion object {
        val FILTER_OPEN_ALL: String = str(R.string.filter_order_open)
        val FILTER_OPEN_NEW: String = str(R.string.filter_order_open_new)
        val FILTER_OPEN_PARTIAL: String = str(R.string.filter_order_open_partial)
        val FILTER_FILLED: String = str(R.string.filter_order_filled)
        val FILTER_CANCELLED: String = str(R.string.filter_order_cancelled)
        val FILTER_EXPIRED: String = str(R.string.filter_order_expired)

        val FILTER_DATES: Array<String> = strArray(R.array.filter_order_dates)
        val FILTER_OPEN_ORDER_STATUS_UI: Array<String> = strArray(R.array.filter_order_open_statuses_ui)
        val FILTER_OPEN_ORDER_STATUS_VALUES: Array<String> = strArray(R.array.filter_order_open_statuses_values)

    }

}