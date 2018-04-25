package org.loopring.looprwallet.hometransfers.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.hometransfers.R

/**
 * Created by Corey Caplan on 4/20/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersAdapter(listener: OnTransferClickListener) : BaseRealmAdapter<LooprTransfer>() {

    private val listener by weakReference(listener)

    override val totalItems: Int? = null

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewTransfersEmptyViewHolder(parent.inflate(R.layout.view_holder_view_transfers_empty))
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewTransfersViewHolder(parent.inflate(R.layout.view_holder_view_transfers))
    }

    override fun getDataOffset() = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprTransfer?) {
        item ?: return // GUARD

        (holder as? ViewTransfersViewHolder)?.bind(item) {
            listener?.onTransferClick(it)
        }
    }

}