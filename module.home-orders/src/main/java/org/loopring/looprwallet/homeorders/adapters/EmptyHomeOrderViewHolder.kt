package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_general_orders_empty.*
import org.loopring.looprwallet.core.models.loopr.orders.OrderFilter
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param orderType The order type to be displayed in this ViewHolder.
 *
 * @see OrderFilter.FILTER_OPEN_ALL
 * @see OrderFilter.FILTER_FILLED
 * @see OrderFilter.FILTER_CANCELLED
 */
class EmptyHomeOrderViewHolder(private val orderType: String, itemView: View?)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind() {
        when (orderType) {
            OrderFilter.FILTER_OPEN_ALL -> emptyOrdersLabel.setText(R.string.empty_general_open_orders)
            OrderFilter.FILTER_FILLED -> emptyOrdersLabel.setText(R.string.empty_general_filled_orders)
            OrderFilter.FILTER_CANCELLED -> emptyOrdersLabel.setText(R.string.empty_general_cancelled_orders)
            else -> throw IllegalArgumentException("Invalid orderType, found: $orderType")
        }
    }

}