package com.caplaninnovations.looprwallet.adapters.orders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caplaninnovations.looprwallet.models.wallet.LooprOrder

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(order: LooprOrder, cancelListener: (LooprOrder) -> Unit) {
        // TODO show cancel button if the order is open; hide otherwise
    }

}