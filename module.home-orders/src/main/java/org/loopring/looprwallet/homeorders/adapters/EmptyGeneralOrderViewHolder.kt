package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_general_orders_empty.*
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param isOpen True if this *ViewHolder* is for open orders or false for past ones (closed)
 */
class EmptyGeneralOrderViewHolder(private val isOpen: Boolean, itemView: View?)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind() = when {
        isOpen -> emptyOrdersLabel.setText(R.string.empty_general_open_orders)
        else -> emptyOrdersLabel.setText(R.string.empty_general_closed_orders)
    }

}