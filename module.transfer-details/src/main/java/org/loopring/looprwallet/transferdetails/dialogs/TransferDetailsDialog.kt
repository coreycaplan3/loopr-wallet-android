package org.loopring.looprwallet.transferdetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_transfer_details.*
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.DateUtility
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.transferdetails.R
import org.loopring.looprwallet.transferdetails.viewmodels.TransferDetailsViewModel
import java.text.DateFormat
import java.util.*

/**
 * Created by Corey on 4/20/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class: To view the details for a transfer and allow the user to be able to open it
 * in a block explorer.
 *
 */
class TransferDetailsDialog : BaseBottomSheetDialog() {

    companion object {

        private const val KEY_TRANSFER = "_TRANSFER"

        fun getInstance(transfer: LooprTransfer) = TransferDetailsDialog().apply {
            arguments = bundleOf(KEY_TRANSFER to transfer.transactionHash)
        }

    }

    private var transferDetailsViewModel: TransferDetailsViewModel? = null
        @Synchronized
        get() {
            if (field != null) {
                return field
            }

            val wallet = walletClient.getCurrentWallet() ?: return null
            field = LooprViewModelFactory.get(this, wallet)
            return field
        }

    private val transactionHash by lazy {
        arguments?.getString(KEY_TRANSFER)!!
    }

    override val layoutResource: Int
        get() = R.layout.dialog_transfer_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferDetailsViewModel?.getTransferByHash(this, transactionHash, ::onDataChange)
    }

    private fun onDataChange(transfer: LooprTransfer) {
        transferDetailsDateLabel.text = DateFormat.getDateTimeInstance()
                .format(Date(transfer.timestamp))

        transferDetailsViewTransactionButton.setOnClickListener { TODO("") }
    }

}