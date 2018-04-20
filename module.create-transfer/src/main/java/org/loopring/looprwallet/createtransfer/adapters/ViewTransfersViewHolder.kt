package org.loopring.looprwallet.createtransfer.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import org.loopring.looprwallet.core.models.transfers.LooprTransfer

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