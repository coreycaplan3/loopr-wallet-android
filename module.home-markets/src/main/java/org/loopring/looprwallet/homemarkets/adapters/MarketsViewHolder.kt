package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import org.loopring.looprwallet.core.models.markets.TradingPair

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    inline fun bind(tradingPair: Any, crossinline clickListener: (Any) -> Unit) {
        itemView.setOnClickListener { clickListener(tradingPair)}
    }

}