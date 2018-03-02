package com.caplaninnovations.looprwallet.adapters.transfers

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show an introduction, if the user has not seen it yet, on how the transfer
 * screen works.
 *
 */
class TutorialTransferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    init {
        // TODO persist user dismissing card to Realm.
    }

    fun bind() {

    }

}