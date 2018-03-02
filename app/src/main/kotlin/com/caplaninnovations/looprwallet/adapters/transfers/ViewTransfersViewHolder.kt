package com.caplaninnovations.looprwallet.adapters.transfers

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caplaninnovations.looprwallet.models.wallet.transfer.LooprTransfer
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(looprTransfer: LooprTransfer) {
        // TODO
    }

}