package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

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
     */
    inline fun bind(order: Any, crossinline onOrderClick: (Any) -> Unit) {
        itemView.setOnClickListener { onOrderClick(order) }
        // TODO bind order
    }

}