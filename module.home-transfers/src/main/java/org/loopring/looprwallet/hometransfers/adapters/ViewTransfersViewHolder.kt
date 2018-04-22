package org.loopring.looprwallet.hometransfers.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import org.loopring.looprwallet.core.models.transfers.LooprTransfer

/**
 * Created by Corey Caplan on 4/20/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    inline fun bind(item: LooprTransfer, onTransferClick: (LooprTransfer) -> Unit) {
        TODO("You know what to do...")
    }

}