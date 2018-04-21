package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_general_order.*

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class GeneralOrderViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    /**
     * Binds the order to this ViewHolder and registers a click listener to the itemView.
     *
     * @param order The order to be bound to this ViewHolder
     * @param showDateHeader True to show the date header for this order or false to hide it
     * @param onOrderClick A click handler that is fired when a user clicks on an order
     */
    inline fun bind(order: Any, showDateHeader: Boolean, crossinline onOrderClick: (Any) -> Unit) {

        generalOrderDateTitle.visibility = when (showDateHeader) {
            true -> View.VISIBLE
            else -> View.GONE
        }

        generalOrderContainer.setOnClickListener {
            onOrderClick(order)
        }

        TODO("BIND ORDER; BE SURE TO INCLUDE OPEN/FULFILLED/EXPIRED")
    }

}