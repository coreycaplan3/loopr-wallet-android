package org.loopring.looprwallet.transferdetails.dialogs

import android.net.Uri
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.view.View
import androidx.net.toUri
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.dialog_transfer_details.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.repositories.contacts.ContactsRepository
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ChromeCustomTabsUtility
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumBlockNumberViewModel
import org.loopring.looprwallet.transferdetails.R
import org.loopring.looprwallet.transferdetails.dagger.transferDetailsLooprComponent
import org.loopring.looprwallet.transferdetails.viewmodels.TransferDetailsViewModel
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum
import java.math.BigInteger
import java.text.DateFormat
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

    private val transferDetailsViewModel: TransferDetailsViewModel by lazy {
        LooprViewModelFactory.get<TransferDetailsViewModel>(this)
    }

    private lateinit var ethereumBlockNumberViewModel: EthereumBlockNumberViewModel

    private val transactionHash by lazy {
        arguments?.getString(KEY_TRANSFER)!!
    }

    @VisibleForTesting
    var looprTransfer: LooprTransfer? = null

    @VisibleForTesting
    var ethBlockNum: EthBlockNum? = null

    @Inject
    lateinit var currencySettings: CurrencySettings

    override val layoutResource: Int
        get() = R.layout.dialog_transfer_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            ethereumBlockNumberViewModel = LooprViewModelFactory.get(it)
        }

        transferDetailsLooprComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ethereumBlockNumberViewModel.getEthereumBlockNumber(this) {
            ethBlockNum = it
            bindStatus()
        }

        transferDetailsViewModel.getTransferByHash(this, transactionHash, ::onDataChange)
    }

    private fun onDataChange(transfer: LooprTransfer) {
        this.looprTransfer = transfer

        // Send / Receive
        val quantityFormatterText: String
        if (transfer.isSend) {
            transferDetailsSendImage.visibility = View.VISIBLE
            transferDetailsReceiveImage.visibility = View.GONE

            quantityFormatterText = str(R.string.formatter_sent)
        } else {
            transferDetailsReceiveImage.visibility = View.VISIBLE
            transferDetailsSendImage.visibility = View.GONE

            quantityFormatterText = str(R.string.formatter_received)
        }

        val contactText = when (transfer.isSend) {
            true -> getString(R.string.from)
            false -> getString(R.string.to)
        }

        val contactAddress = transfer.contactAddress
        transferDetailsContactFromLabel.text = contactText
        transferDetailsContactLabel.text = contactAddress

        transferDetailsContactLabel.setOnClickListener {
            val url = ChromeCustomTabsUtility.ETHERSCAN_ADDRESS_URI + transfer.contactAddress
            ChromeCustomTabsUtility.getInstance(it.context)
                    .launchUrl(it.context, url.toUri())
        }

        async(IO) {
            val contactName = ContactsRepository().getContactByAddressNow(contactAddress, IO)?.name

            if (contactName != null) {
                async(UI) {
                    transferDetailsContactLabel.text = contactText.format(contactName)
                }
            }
        }

        // Quantity
        val quantity = transfer.numberOfTokens.formatAsToken(currencySettings, transfer.token)
        transferDetailsQuantityLabel.text = quantityFormatterText.format(quantity)

        // USD Value
        val usdValue = transfer.transactionFeeUsdValue.formatAsCurrency(currencySettings)
        transferDetailsPriceLabel.text = usdValue

        // Date
        val dateFormat = DateFormat.getDateTimeInstance()
        transferDetailsDateLabel.text = dateFormat.format(transfer.timestamp)

        // Explorer
        transferDetailsViewTransactionButton.setOnClickListener {
            val url = ChromeCustomTabsUtility.ETHERSCAN_TX_URI + transfer.transactionHash
            ChromeCustomTabsUtility.getInstance(it.context)
                    .launchUrl(it.context, url.toUri())
        }

        // Status
        bindStatus()
    }

    private fun bindStatus() {
        val transfer = looprTransfer ?: return
        val ethereumBlockNumber = ethBlockNum?.blockNumber ?: return
        val transferBlockNumber = transfer.blockNumber

        val status: String
        val color: Int
        when {
            transferBlockNumber == null || ethereumBlockNumber - transferBlockNumber < BigInteger.ZERO -> {
                //  The difference may be < 0 if the ethBlockNum is stale
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