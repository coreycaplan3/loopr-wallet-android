package com.caplaninnovations.looprwallet.dialogs

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.viewmodels.WalletGeneratorViewModel
import com.caplaninnovations.looprwallet.models.wallet.creation.PasswordBasedWallet
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationKeystore
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import com.caplaninnovations.looprwallet.utilities.WalletGeneratorUtility
import com.caplaninnovations.looprwallet.validators.PasswordMatcherValidator
import kotlinx.android.synthetic.main.dialog_confirm_password.*

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ConfirmPasswordDialog : BottomSheetDialogFragment() {

    companion object {

        val TAG: String = ConfirmPasswordDialog::class.java.simpleName
        const val KEY_WALLET = "_WALLET"

        fun createInstance(walletCreationKeystore: WalletCreationKeystore): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply { putParcelable(KEY_WALLET, walletCreationKeystore) }
            }
        }

        fun createInstance(walletCreationPhrase: WalletCreationPhrase): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply { putParcelable(KEY_WALLET, walletCreationPhrase) }
            }
        }
    }

    private val walletCreationKeystore: WalletCreationKeystore? by lazy {
        val wallet: Parcelable? = arguments?.getParcelable(KEY_WALLET)
        if (wallet is WalletCreationKeystore) {
            wallet
        } else {
            null
        }

    }

    private val walletCreationPhrase: WalletCreationPhrase? by lazy {
        val wallet: Parcelable? = arguments?.getParcelable(KEY_WALLET)
        if (wallet is WalletCreationPhrase) {
            wallet
        } else {
            null
        }
    }

    private val passwordMatcherValidator: PasswordMatcherValidator by lazy {
        val password = listOf<PasswordBasedWallet?>(walletCreationKeystore, walletCreationPhrase)
                .first { it != null }!!
                .getWalletPassword()

        PasswordMatcherValidator(confirmPasswordInputLayout, this::onPasswordChange, password)
    }

    val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val walletList = listOf(
                walletCreationKeystore,
                walletCreationPhrase
        )
        if (walletList.filter { it != null }.size != 1) {
            throw IllegalStateException("Invalid argument passed!")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirm_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            it.setOnShowListener {
                val bottomSheetDialog = dialog as? BottomSheetDialog
                val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        cancelButton.setOnClickListener { dismiss() }

        onPasswordChange()

        when {
            walletCreationKeystore != null ->
                confirmButton.setOnClickListener {
                    clickListenerForKeystore(it, walletCreationKeystore!!)
                }

            walletCreationPhrase != null ->
                confirmButton.setOnClickListener {
                    clickListenerForPhrase(it, walletCreationPhrase!!)
                }
        }

        WalletGeneratorUtility.setupForFragment(walletGeneratorViewModel, this)
    }

    // MARK - Private Methods

    private fun onPasswordChange() {
        confirmButton.isEnabled = passwordMatcherValidator.isValid()
    }

    private fun clickListenerForKeystore(view: View, wallet: WalletCreationKeystore) {
        val walletDirectory = view.context.filesDir

        walletGeneratorViewModel.createKeystoreWallet(wallet.walletName, wallet.password, walletDirectory)
    }

    private fun clickListenerForPhrase(view: View, wallet: WalletCreationPhrase) {
        //TODO
    }

}