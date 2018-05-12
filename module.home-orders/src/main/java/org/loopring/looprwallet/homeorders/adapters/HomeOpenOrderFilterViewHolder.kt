package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_open_order_filter.*

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for open order tab.
 */
class HomeOpenOrderFilterViewHolder(
        itemView: View,
        onCancelAllClickListener: ((View) -> Unit)
) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    init {
        // CANCEL ALL BUTTON
        openOrderCancelAllButton.setOnClickListener(onCancelAllClickListener)
    }

    fun bind() {

    }

}