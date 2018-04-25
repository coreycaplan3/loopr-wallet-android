package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import org.loopring.looprwallet.core.models.markets.TradingPair

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    inline fun bind(tradingPair: TradingPair, crossinline clickListener: (TradingPair) -> Unit) {
        // TODO
        itemView.setOnClickListener { clickListener(tradingPair)}
    }

}