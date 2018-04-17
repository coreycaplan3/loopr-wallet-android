package org.loopring.looprwallet.homemywallet.fragments

import android.os.Bundle
import android.support.v7.widget.TooltipCompat
import android.view.View
import kotlinx.android.synthetic.main.card_wallet_information.*
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homemywallet.R
import android.content.Intent
import android.support.v7.app.AlertDialog
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment.OnSecurityConfirmedListener
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter.BottomNavigationReselectedLister


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableToolbarCollapsing()

        TooltipCompat.setTooltipText(shareAddressButton, str(R.string.share_your_address))
        TooltipCompat.setTooltipText(showPrivateKeyButton, str(R.string.reveal_private_key))

        val currentWallet = walletClient.getCurrentWallet()
        if (currentWallet != null) {
            when {
                currentWallet.keystoreContent != null -> {
                    TooltipCompat.setTooltipText(showWalletUnlockMechanismButton, str(R.string.copy_keystore_content))
                    showWalletUnlockMechanismButton.visibility = View.VISIBLE
                }
                currentWallet.passphrase != null -> {
                    TooltipCompat.setTooltipText(showWalletUnlockMechanismButton, str(R.string.view_your_passphrase))
                    showWalletUnlockMechanismButton.visibility = View.VISIBLE
                }
                else -> {
                    showWalletUnlockMechanismButton.visibility = View.GONE
                }
            }
        }

        showPrivateKeyButton.setOnClickListener { onShowPrivateKeyClick() }

        showWalletUnlockMechanismButton.setOnClickListener { onShowWalletUnlockMechanismClick() }
    }

    override fun onBottomNavigationReselected() {
        logd("Wallet Reselected!")
        // Scroll to the top of the NestedScrollView
        fragmentContainer.scrollTo(0, 0)
    }

    override fun onSecurityConfirmed(parameter: Int): Unit = when (parameter) {
        TYPE_VIEW_PRIVATE_KEY -> {
            TODO("Show private key with QR Code and plaintext")
        }
        TYPE_VIEW_KEYSTORE -> walletClient.getCurrentWallet().ifNotNull {
            val sharingIntent = Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, str(R.string.formatter_keystore).format(it.walletName))
                    .putExtra(Intent.EXTRA_TEXT, it.keystoreContent)

            startActivity(Intent.createChooser(sharingIntent, str(R.string.share_keystore_via)))
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
                    .setTitle(R.string.your_passphrase)
                    .setMessage(formattedPhrase)
                    .create()
                    .show()
        }
        else -> {
            loge("Invalid parameter, found: $parameter", IllegalArgumentException())
        }
    }

    // MARK - Private Methods

    private fun onShowPrivateKeyClick() {
        val fragment = ConfirmOldSecurityFragment.createViewPrivateKeyInstance(TYPE_VIEW_PRIVATE_KEY)
        pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG)
    }

    private fun onShowWalletUnlockMechanismClick() = walletClient.getCurrentWallet().ifNotNull {
        val fragment = when {
            it.keystoreContent != null -> ConfirmOldSecurityFragment.createViewWalletUnlockMechanismInstance(TYPE_VIEW_KEYSTORE)
            it.passphrase != null -> ConfirmOldSecurityFragment.createViewWalletUnlockMechanismInstance(TYPE_VIEW_PHRASE)
            else -> throw IllegalArgumentException("Invalid unlockWallet mechanism!")
        }
        pushFragmentTransaction(fragment, ConfirmOldSecurityFragment.TAG)
    }

}