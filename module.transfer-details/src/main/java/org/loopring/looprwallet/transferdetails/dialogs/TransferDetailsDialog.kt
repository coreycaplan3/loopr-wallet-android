package org.loopring.looprwallet.transferdetails.dialogs

import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_transfer_details.*
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.blockchain.EthereumBlockNumber
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ChromeCustomTabsUtility
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumBlockNumberViewModel
import org.loopring.looprwallet.transferdetails.R
import org.loopring.looprwallet.transferdetails.dagger.transferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.viewmodels.TransferDetailsViewModel
import java.math.BigInteger
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

        val TAG: String = TransferDetailsDialog::class.java.simpleName

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

    private lateinit var ethereumBlockNumberViewModel: EthereumBlockNumberViewModel

    private val transactionHash by lazy {
        arguments?.getString(KEY_TRANSFER)!!
    }

    var looprTransfer: LooprTransfer? = null

    var ethereumBlockNumber: EthereumBlockNumber? = null

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

        ethereumBlockNumberViewModel = ViewModelProviders.of(activity!!)
                .get(EthereumBlockNumberViewModel::class.java)

        ethereumBlockNumberViewModel.getEthereumBlockNumber(this) {
            ethereumBlockNumber = it
            bindStatus()
        }

        transferDetailsViewModel?.getTransferByHash(this, transactionHash, ::onDataChange)
    }

    private fun onDataChange(transfer: LooprTransfer) {
        this.looprTransfer = transfer

        // Quantity
        val quantity = transfer.numberOfTokens.formatAsToken(currencySettings, transfer.token)
        transferDetailsQuantityLabel.text = str(R.string.formatter_received).format(quantity)

        // USD Value
        val usdValue = transfer.transactionFeeUsdValue.formatAsCurrency(currencySettings)
        transferDetailsPriceLabel.text = usdValue

        // Date
        val dateFormat = DateFormat.getDateTimeInstance()
        transferDetailsDateLabel.text = dateFormat.format(transfer.timestamp)

        // Explorer
        transferDetailsViewTransactionButton.setOnClickListener {
            ChromeCustomTabsUtility.getInstance(it.context)
                    .launchUrl(it.context, Uri.parse("https://etherscan.io/tx/${transfer.transactionHash}"))
        }

        // Status
        bindStatus()
    }

    fun bindStatus() {
        val transfer = looprTransfer ?: return
        val ethereumBlockNumber = ethereumBlockNumber?.blockNumber ?: return
        val transferBlockNumber = transfer.blockNumber

        val status: String
        val color: Int
        when {
            transferBlockNumber == null || ethereumBlockNumber - transferBlockNumber < BigInteger.ZERO -> {
                //  The difference may be < 0 if the ethereumBlockNumber is stale
                status = str(R.string.waiting_to_be_mined)
                color = col(R.color.red_400, activity)
            }
            ethereumBlockNumber - transferBlockNumber < BigInteger("12") -> {
                status = str(R.string.pending)
                color = col(R.color.amber_500, activity)
            }
            else -> {
                status = str(R.string.complete)
                color = col(R.color.green_400, activity)
            }
        }

        transferDetailsStatusLabel.text = status
        transferDetailsStatusLabel.setTextColor(color)
    }

}