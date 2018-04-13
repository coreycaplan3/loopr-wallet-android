package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Corey on 4/13/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show a *ViewHolder* if the markets are unable to load (since they can't be
 * empty otherwise)
 *
 * @param listener The click listener that will be used to prompt a retry for the user.
 */
class MarketsEmptyViewHolder(itemView: View, listener: (View) -> Unit)
    : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener(listener)
    }

}