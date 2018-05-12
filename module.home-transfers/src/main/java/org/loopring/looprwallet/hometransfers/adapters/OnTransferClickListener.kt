package org.loopring.looprwallet.hometransfers.adapters

import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer

/**
 * Created by Corey on 4/20/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: For passing click events from [ViewTransfersAdapter] to the implementing
 * fragment.
 *
 */
interface OnTransferClickListener {

    /**
     * Called when a [LooprTransfer] item is clicked
     *
     * @param looprTransfer The transfer item that was clicked
     */
    fun onTransferClick(looprTransfer: LooprTransfer)
}