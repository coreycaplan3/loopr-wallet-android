package com.caplaninnovations.looprwallet.adapters.markets

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caplaninnovations.looprwallet.models.wallet.TradingPair

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(tradingPair: TradingPair, clickListener: (TradingPair) -> Unit) {
        itemView.setOnClickListener {
            clickListener(tradingPair)
        }
    }

}