package org.loopring.looprwallet.homemywallet.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.support.v7.widget.TooltipCompat
import android.view.MenuItem
import android.view.View
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.card_account_balances.*
import kotlinx.android.synthetic.main.card_contacts.*
import kotlinx.android.synthetic.main.card_wallet_information.*
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.barcode.activities.ViewBarcodeActivity
import org.loopring.looprwallet.barcode.utilities.BarcodeUtility
import org.loopring.looprwallet.contacts.activities.ViewContactsActivity
import org.loopring.looprwallet.contacts.dialogs.CreateContactDialog
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment.OnSecurityConfirmedListener
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenBalanceViewModel
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.homemywallet.R
import org.loopring.looprwallet.homemywallet.dagger.homeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dialogs.ShowBarcodeDialog
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity
import org.loopring.looprwallet.viewbalances.activities.ViewBalancesActivity
import javax.inject.Inject
import kotlin.math.roundToInt
import androidx.net.toUri
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.utilities.ChromeCustomTabsUtility


/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class HomeMyWalletFragment : BaseFragment(), BottomNavigationReselectedLister,
        OnSecurityConfirmedListener {

    companion object {
        const val TYPE_VIEW_PRIVATE_KEY = 1
        const val TYPE_VIEW_KEYSTORE = 2
        const val TYPE_VIEW_PHRASE = 3
    }

    override val layoutResource: Int
        get() = R.layout.fragment_my_wallet

    @Inject
    lateinit var securitySettings: SecuritySettings

    @Inject
    lateinit var currencySettings: CurrencySettings

    private val tokenBalanceViewModel: EthereumTokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenBalanceViewModel>(activity!!, "balance")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeMyWalletLooprComponent.inject(this)

        toolbarDelegate?.onCreateOptionsMenu = createOptionsMenu
        toolbarDelegate?.onOptionsItemSelected = optionsItemSelected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableToolbarCollapsing()

        setupWalletInformation()

        setupBalanceAndContactInformation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureActivity.handleActivityResult(requestCode, resultCode, data) { type, value ->
            when (type) {
                BarcodeCaptureActivity.TYPE_PUBLIC_KEY -> {
                    CreateTransferActivity.route(this, value)
                }
                BarcodeCaptureActivity.TYPE_TRADING_PAIR -> {
                    TradingPairDetailsActivity.route(TradingPair(value), this)
                }
            }
        }
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
        // Scroll to the top of the NestedScrollView
        homeMyWalletScrollContainer.smoothScrollTo(0, 0)
    }

    private val createOptionsMenu: (Toolbar?) -> Unit = {
        it?.menu?.clear()
        it?.inflateMenu(R.menu.menu_home)
    }

    private val optionsItemSelected: (MenuItem?) -> Boolean = { item ->
        when (item?.itemId) {
            android.R.id.home -> activity?.onOptionsItemSelected(item) ?: false
            R.id.menuMainScanQrCode -> {
                BarcodeCaptureActivity.route(this, arrayOf(BarcodeCaptureActivity.TYPE_PUBLIC_KEY, BarcodeCaptureActivity.TYPE_TRADING_PAIR))
                true
            }
            R.id.menuMainSettings -> {
                SettingsActivity.route(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSecurityConfirmed(parameter: Int): Unit = when (parameter) {
        TYPE_VIEW_PRIVATE_KEY -> {
            val privateKey = walletClient.getCurrentWallet()?.credentials?.ecKeyPair?.privateKey
            privateKey?.let {
                ShowBarcodeDialog.getInstance(it.toString(16))
                        .show(requireFragmentManager(), ShowBarcodeDialog.TAG)
            } ?: Unit
        }
        TYPE_VIEW_KEYSTORE -> walletClient.getCurrentWallet().ifNotNull {
            AlertDialog.Builder(context!!)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.help_export_keystore)
                    .setPositiveButton(R.string.export_anyway) { dialog, _ ->
                        dialog.dismiss()
                        val sharingIntent = Intent(Intent.ACTION_SEND)
                                .setType("text/plain")
                                .putExtra(Intent.EXTRA_SUBJECT, str(R.string.formatter_keystore).format(it.walletName))
                                .putExtra(Intent.EXTRA_TEXT, it.keystoreContent)

                        startActivity(Intent.createChooser(sharingIntent, str(R.string.share_keystore_via)))
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
        }
        TYPE_VIEW_PHRASE -> walletClient.getCurrentWallet().ifNotNull {
            val builder = StringBuilder()
            it.passphrase!!.forEachIndexed { index, value ->
                builder.append(index)
                        .append(". ")
                        .append(value)
                if (index < it.passphrase!!.size - 1) {
                    builder.append("\n")
                }
            }
            val formattedPhrase = str(R.string.formatter_your_12_words_are).format(builder.toString())

            val context = context ?: return@ifNotNull
            AlertDialog.Builder(context)
                    .setTitle(R.string.your_phrase)
                    .setMessage(formattedPhrase)
                    .show()
        }
        else -> {
            loge("Invalid parameter, found: $parameter", IllegalArgumentException())
        }
    }

    // MARK - Private Methods

    private fun setupWalletInformation() = walletClient.getCurrentWallet()?.let { wallet ->

        addressLabel.setOnClickListener {
            val url = ChromeCustomTabsUtility.ETHERSCAN_ADDRESS_URI + wallet.credentials.address
            ChromeCustomTabsUtility.getInstance(it.context)
                    .launchUrl(it.context, url.toUri())
        }

        // Setup Tooltips
        TooltipCompat.setTooltipText(shareAddressButton, str(R.string.share_your_address))
        TooltipCompat.setTooltipText(showPrivateKeyButton, str(R.string.reveal_private_key))

        // Setup the showWalletUnlockMechanismButton, based on how the type of wallet
        when {
            wallet.keystoreContent != null -> {
                TooltipCompat.setTooltipText(showWalletUnlockMechanismButton, str(R.string.copy_keystore_content))
                showWalletUnlockMechanismButton.visibility = View.VISIBLE
            }
            wallet.passphrase != null -> {
                TooltipCompat.setTooltipText(showWalletUnlockMechanismButton, str(R.string.view_your_passphrase))
                showWalletUnlockMechanismButton.visibility = View.VISIBLE
            }
            else -> {
                showWalletUnlockMechanismButton.visibility = View.GONE
            }
        }

        // Setup the Share button
        shareAddressButton.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, wallet.credentials.address)
            }

            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_my_address)))
        }

        // Setup the Address Barcode
        try {
            val address = wallet.credentials.address
            val dimensions = resources.getDimension(R.dimen.qr_code_dimensions).roundToInt()
            val barcode = BarcodeUtility.encodeTextToBitmap(address, dimensions)
            addressBarcodeImage.setImageBitmap(barcode)
            addressLabel.text = address
        } catch (e: Throwable) {
            addressLabel.text = str(R.string.error_creating_qr_code)
        }

        addressBarcodeImage.setOnClickListener {
            val titleText = str(R.string.my_address)
            val barcodeText = wallet.credentials.address
            ViewBarcodeActivity.route(activity!!, titleText, barcodeText)

        }
        showPrivateKeyButton.setOnClickListener { onShowPrivateKeyClick() }
        showWalletUnlockMechanismButton.setOnClickListener { onShowWalletUnlockMechanismClick() }

    }

    private fun setupBalanceAndContactInformation() {
        TooltipCompat.setTooltipText(myWalletShowTokensButton, str(R.string.view_all_your_balances))
        TooltipCompat.setTooltipText(addContactButton, str(R.string.add_a_new_contact))
        TooltipCompat.setTooltipText(viewAllContactsButton, str(R.string.view_all_your_contacts))

        myWalletShowTokensButton.setOnClickListener {
            activity?.ifNotNull { ViewBalancesActivity.route(it) }
        }

        addContactButton.setOnClickListener {
            CreateContactDialog.getInstance(null)
                    .show(fragmentManager, CreateContactDialog.TAG)
        }

        viewAllContactsButton.setOnClickListener {
            activity?.ifNotNull { ViewContactsActivity.route(it) }
        }

        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return
        setupOfflineFirstStateAndErrorObserver(tokenBalanceViewModel, fragmentContainer)
        tokenBalanceViewModel.getAllTokensWithBalances(this, address, ::onTokenBalancesChange)
    }

    private fun onTokenBalancesChange(tokenBalances: OrderedRealmCollection<LooprToken>) {
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return

        tokenBalances.firstOrNull { it.identifier == LooprToken.ETH.identifier }?.let { token ->
            val balance = token.findAddressBalance(address)
            if (balance != null) {
                ethereumBalanceLabel.text = balance.formatAsToken(currencySettings, token)
            }
        }

        val builder = StringBuilder()
        val onlyTokenBalances = tokenBalances.filter { it.identifier != LooprToken.ETH.identifier }

        onlyTokenBalances.forEachIndexed { index, token ->
            when {
                index <= 2 -> {
                    val balance = token.findAddressBalance(address)
                            ?: return@forEachIndexed

                    builder.append(balance.formatAsToken(currencySettings, token))

                    // We're before the last index and before the second-to-last item
                    if (index < onlyTokenBalances.size - 1) builder.append("\n")
                }
                index == 3 -> builder.append("\n").append(str(R.string.ellipsis))
            }
        }

        tokenBalanceLabel.text = builder.toString()
    }

    private fun onShowPrivateKeyClick() = when {
        securitySettings.isSecurityActive() -> {
            val fragment = ConfirmOldSecurityFragment.getViewPrivateKeyInstance(TYPE_VIEW_PRIVATE_KEY)
            pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG, FragmentTransactionController.ANIMATION_VERTICAL)
        }
        else -> onSecurityConfirmed(TYPE_VIEW_PRIVATE_KEY)
    }

    private fun onShowWalletUnlockMechanismClick() = walletClient.getCurrentWallet().ifNotNull { wallet ->
        val parameter = when {
            wallet.keystoreContent != null -> TYPE_VIEW_KEYSTORE
            wallet.passphrase != null -> TYPE_VIEW_PHRASE
            else -> {
                loge("Invalid unlockWallet mechanism!", IllegalArgumentException())
                return
            }
        }

        if (securitySettings.isSecurityActive()) {
            val fragment = ConfirmOldSecurityFragment.createViewWalletUnlockMechanismInstance(parameter)
            pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG, FragmentTransactionController.ANIMATION_VERTICAL)
        } else {
            onSecurityConfirmed(parameter)
        }
    }
}