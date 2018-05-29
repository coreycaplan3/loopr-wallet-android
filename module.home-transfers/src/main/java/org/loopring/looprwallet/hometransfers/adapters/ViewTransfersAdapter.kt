package org.loopring.looprwallet.hometransfers.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.loopr.paging.DefaultLooprPagerAdapter
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.hometransfers.R

/**
 * Created by Corey Caplan on 4/20/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersAdapter(private val listener: OnTransferClickListener) : BaseRealmAdapter<LooprTransfer>() {

    override var pager: LooprAdapterPager<LooprTransfer> = DefaultLooprPagerAdapter()

    init {
        containsHeader = false
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_view_transfers_empty, parent, false)
        return ViewTransfersEmptyViewHolder(view)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_view_transfers, parent, false)
        return ViewTransfersViewHolder(view, ::onTransferClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprTransfer?) {
        item ?: return // GUARD

        (holder as? ViewTransfersViewHolder)?.bind(item)
    }

    private fun onTransferClick(index: Int) {
        val position = index + dataOffsetPosition
        val transfer = data?.get(position) ?: return

        listener.onTransferClick(transfer)
    }

}