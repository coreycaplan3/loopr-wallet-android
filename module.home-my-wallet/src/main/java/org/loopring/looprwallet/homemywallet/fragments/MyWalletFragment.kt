package org.loopring.looprwallet.homemywallet.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.TooltipCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.card_account_balances.*
import kotlinx.android.synthetic.main.card_wallet_information.*
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import org.loopring.looprwallet.barcode.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.barcode.utilities.BarcodeUtility
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment.OnSecurityConfirmedListener
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthTokenBalanceViewModel
import org.loopring.looprwallet.createtransfer.activities.CreateTransferActivity
import org.loopring.looprwallet.homemywallet.R
import org.loopring.looprwallet.homemywallet.dagger.homeMyWalletLooprComponent
import org.loopring.looprwallet.homemywallet.dialogs.ShowBarcodeDialog
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class MyWalletFragment : BaseFragment(), BottomNavigationReselectedLister,
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

    val tokenBalanceViewModel: EthTokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthTokenBalanceViewModel>(this@MyWalletFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeMyWalletLooprComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableToolbarCollapsing()

        walletClient.getCurrentWallet()?.let { wallet ->
            tokenBalanceViewModel.getAllTokensWithBalances(this, wallet.credentials.address, ::onTokenBalancesChange)

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

            // Setup the Address Barcode
            try {
                val address = wallet.credentials.address
                val dimensions = resources.getDimension(R.dimen.barcode_dimensions).roundToInt()
                val barcode = BarcodeUtility.encodeTextToBitmap(address, dimensions)
                addressBarcodeImage.setImageBitmap(barcode)
                addressLabel.text = address
            } catch (e: Throwable) {
                addressLabel.text = str(R.string.error_creating_qr_code)
            }

        }

        showPrivateKeyButton.setOnClickListener { onShowPrivateKeyClick() }
        showWalletUnlockMechanismButton.setOnClickListener { onShowWalletUnlockMechanismClick() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BarcodeCaptureActivity.handleActivityResult(requestCode, resultCode, data) { type, value ->
            when(type) {
                BarcodeCaptureActivity.TYPE_PUBLIC_KEY -> {
                    CreateTransferActivity.route(this, value)
                }
                BarcodeCaptureActivity.TYPE_TRADING_PAIR -> {
                    val tradingPair = TradingPair.createFromMarket(value)
                    TradingPairDetailsActivity.route(tradingPair, this)
                }
            }
        }
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
        // Scroll to the top of the NestedScrollView
        fragmentContainer.scrollTo(0, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
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
                    .create()
                    .show()
        }
        else -> {
            loge("Invalid parameter, found: $parameter", IllegalArgumentException())
        }
    }

    // MARK - Private Methods

    private fun onTokenBalancesChange(tokenBalances: OrderedRealmCollection<LooprToken>) {
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return

        tokenBalances.firstOrNull { it.identifier == LooprToken.ETH.identifier }?.let {
            val balance = it.findAddressBalance(address)?.balance?.toPlainString()
            if (balance != null) {
                @SuppressLint("SetTextI18n")
                ethereumBalanceLabel.text = "$balance ${it.ticker}"
            }
        }

        val builder = StringBuilder()
        val onlyTokenBalances = tokenBalances.filter { it.identifier != LooprToken.ETH.identifier }

        onlyTokenBalances.forEachIndexed { index, item ->
            if (index < 5) {
                val balance = item.findAddressBalance(address)

                balance?.let {
                    builder.append(it.balance?.toPlainString())
                            .append(" ")
                            .append(item.ticker)

                    // We're before the 5th position (4th index) and before the second-to-last item
                    if (index < 4 && index < onlyTokenBalances.size - 1) builder.append("\n")
                }
            }

            if (index >= 5) {
                builder.append("\n")
                        .append(str(R.string.ellipsis))
            }
        }
    }

    private fun onShowPrivateKeyClick() = when {
        securitySettings.isSecurityActive() -> {
            val fragment = ConfirmOldSecurityFragment.getViewPrivateKeyInstance(TYPE_VIEW_PRIVATE_KEY)
            pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG)
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
            pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG)
        } else {
            onSecurityConfirmed(parameter)
        }
    }
}