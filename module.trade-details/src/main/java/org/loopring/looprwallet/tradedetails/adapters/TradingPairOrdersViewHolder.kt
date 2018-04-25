package org.loopring.looprwallet.tradedetails.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.models.order.LooprOrder

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show a ViewHolder which represents a single order for a specific trading
 * pair.
 */
class TradingPairOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    inline fun onBind(order: LooprOrder, crossinline cancelListener: (LooprOrder) -> Unit) {
        // TODO show cancel button if the order is open; hide otherwise
    }

}