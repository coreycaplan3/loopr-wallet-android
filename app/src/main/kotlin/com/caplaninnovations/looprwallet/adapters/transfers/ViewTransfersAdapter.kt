package com.caplaninnovations.looprwallet.adapters.transfers

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.settings.TutorialTracker
import com.caplaninnovations.looprwallet.models.wallet.transfer.LooprTransfer
import com.caplaninnovations.looprwallet.utilities.inflate
import io.realm.RealmList

/**
 * Created by Corey Caplan on 2/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersAdapter(
        private val tutorialTracker: TutorialTracker,
        var transferList: RealmList<LooprTransfer>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_TUTORIAL = 1
        private const val TYPE_TRANSFER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && !tutorialTracker.isViewTransfersDismissed) {
            // We are in the first position and the tutorial has NOT been dismissed
            TYPE_TUTORIAL
        } else {
            TYPE_TRANSFER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_TUTORIAL) {
            TutorialTransferViewHolder(parent.inflate(R.layout.view_holder_transfer_tutorial))
        } else {
            ViewTransfersViewHolder(parent.inflate(R.layout.view_holder_transfer))
        }
    }

    override fun getItemCount(): Int {
        val type = getItemViewType(0)
        return when (type) {
            TYPE_TUTORIAL -> 1 + transferList.size
            TYPE_TRANSFER -> transferList.size
            else -> throw IllegalStateException("Invalid type, found $type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? ViewTransfersViewHolder)?.let {
            val indexOffset = calculateIndexOffset(position)
            it.bind(transferList[indexOffset]!!)
        }

        (holder as? TutorialTransferViewHolder)?.bind()
    }

    // MARK - Private Methods

    private fun calculateIndexOffset(position: Int): Int {
        val type = getItemViewType(0)
        return when (type) {
            TYPE_TUTORIAL -> position - 1
            TYPE_TRANSFER -> position
            else -> throw IllegalStateException("Invalid type, found $type")
        }
    }

}