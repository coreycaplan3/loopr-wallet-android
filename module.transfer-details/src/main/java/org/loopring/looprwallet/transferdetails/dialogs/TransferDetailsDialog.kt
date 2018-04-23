package org.loopring.looprwallet.transferdetails.dialogs

import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_transfer_details.*
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.DateUtility
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.transferdetails.R
import org.loopring.looprwallet.transferdetails.dagger.transferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.viewmodels.TransferDetailsViewModel
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

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

    @Inject
    lateinit var currencySettings: CurrencySettings

    override val layoutResource: Int
        get() = R.layout.dialog_transfer_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transferDetailsLooprComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferDetailsViewModel?.getTransferByHash(this, transactionHash, ::onDataChange)
    }

    private fun onDataChange(transfer: LooprTransfer) {

        // Quantity
        val quantity = transfer.numberOfTokens.formatAsToken(currencySettings, transfer.token)
        transferDetailsQuantityLabel.text = str(R.string.formatter_received).format(quantity)

        // USD Value
        val usdValue = transfer.transactionFeeUsdValue.formatAsCurrency(currencySettings)
        transferDetailsPriceLabel.text = usdValue

        // Date
        val dateFormat = DateFormat.getDateTimeInstance()
        transferDetailsDateLabel.text = dateFormat.format(Date(transfer.timestamp))

        // Explorer
        transferDetailsViewTransactionButton.setOnClickListener {
            TODO("")
        }

        // status
        transferDetailsStatusLabel
        TODO("BIND DATA TO VIEW MODEL")
    }

}