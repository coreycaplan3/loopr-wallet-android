package org.loopring.looprwallet.core.models.loopr.orders

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.models.loopr.filters.PagingFilter
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To store the possible predicates for filtering orders.
 *
 * @see FILTER_OPEN_ALL
 * @see FILTER_OPEN_NEW
 * @see FILTER_OPEN_PARTIAL
 * @see FILTER_FILLED
 * @see FILTER_CANCELLED
 * @see FILTER_EXPIRED
 *
 * @property address The address whose orders should be retrieved or *null* for all addresses
 * @property market The market whose orders should be retrieved or *null* for all tickers, inputted
 * as LRC-WETH.
 * @property status The status filter to apply. An example is [FILTER_OPEN_ALL].
 * @property pageNumber The current page number. Starts at 1 and keeps going until the final page
 * is reached.
 */
@Parcelize
data class OrderSummaryFilter(
        val address: String?,
        val market: String?,
        val status: String,
        override var pageNumber: Int
) : Parcelable, PagingFilter {

    companion object {

        const val ITEMS_PER_PAGE = 50

        val FILTER_OPEN_ALL: String = str(R.string.filter_order_open)
        val FILTER_OPEN_NEW: String = str(R.string.filter_order_open_new)
        val FILTER_OPEN_PARTIAL: String = str(R.string.filter_order_open_partial)
        val FILTER_FILLED: String = str(R.string.filter_order_filled)
        val FILTER_CANCELLED: String = str(R.string.filter_order_cancelled)
        val FILTER_EXPIRED: String = str(R.string.filter_order_expired)

    }

}