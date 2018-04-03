package org.loopring.looprwallet.home

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caplaninnovations.looprwallet.models.order.LooprOrder

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
internal class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(order: LooprOrder, cancelListener: (LooprOrder) -> Unit) {
        // TODO show cancel button if the order is open; hide otherwise
    }

}