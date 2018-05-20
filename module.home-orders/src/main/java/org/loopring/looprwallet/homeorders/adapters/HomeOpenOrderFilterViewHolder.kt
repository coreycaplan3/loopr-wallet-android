package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import io.realm.OrderedRealmCollection
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_open_order_filter.*
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for open order tab.
 */
class HomeOpenOrderFilterViewHolder(
        itemView: View,
        onCancelAllClickListener: (View) -> Unit
) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    init {
        // CANCEL ALL BUTTON
        openOrderCancelAllButton.setOnClickListener(onCancelAllClickListener)
    }

    fun bind(data: OrderedRealmCollection<AppLooprOrder>?) {
        // If the data is null, invalid or empty --> the button is disabled
        openOrderCancelAllButton.isEnabled = !(data == null || !data.isValid || data.size == 0)
    }

}