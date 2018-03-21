package com.caplaninnovations.looprwallet.dialogs

import android.app.Dialog
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
import com.caplaninnovations.looprwallet.activities.SignInActivity
import com.caplaninnovations.looprwallet.models.wallet.creation.PasswordBasedWallet
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationKeystore
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import com.caplaninnovations.looprwallet.validators.PasswordMatcherValidator
import kotlinx.android.synthetic.main.dialog_confirm_password.*

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A dialog that prompts the user to reenter his/her text password.
 *
 */
class ConfirmPasswordDialog : BottomSheetDialogFragment() {

    /**
     * An interface used to communicate with the activity that a password has been confirmed
     */
    interface Communicator {

        /**
         * Called when the password has been confirmed by the user and is able to proceed with the
         * operation.
         */
        fun onPasswordConfirmed()
    }

    companion object {

        val TAG: String = ConfirmPasswordDialog::class.java.simpleName

        private const val KEY_CURRENT_FRAGMENT_TAG = "_CURRENT_FRAGMENT_TAG"

        const val KEY_WALLET = "_WALLET"

        /**
         * @param currentFragmentTag The tag of the currently active fragment. Used to communicate
         * back to it, after the user confirms their password.
         * @param walletCreationKeystore The wallet that the user is creating.
         */
        fun createInstance(currentFragmentTag: String, walletCreationKeystore: WalletCreationKeystore): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_CURRENT_FRAGMENT_TAG, currentFragmentTag)
                    putParcelable(KEY_WALLET, walletCreationKeystore)
                }
            }
        }

        /**
         * @param currentFragmentTag The tag of the currently active fragment. Used to communicate
         * back to it, after the user confirms their password.
         * @param walletCreationPhrase The wallet that the user is creating.
         */
        fun createInstance(currentFragmentTag: String, walletCreationPhrase: WalletCreationPhrase): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_CURRENT_FRAGMENT_TAG, currentFragmentTag)
                    putParcelable(KEY_WALLET, walletCreationPhrase)
                }
            }
        }
    }

    private val currentFragmentTag: String by lazy {
        arguments!!.getString(KEY_CURRENT_FRAGMENT_TAG)
    }

    private val passwordBasedWallet: PasswordBasedWallet by lazy {
        arguments!!.getParcelable<Parcelable>(KEY_WALLET) as PasswordBasedWallet
    }

    private val passwordMatcherValidator: PasswordMatcherValidator by lazy {
        val password = passwordBasedWallet.getWalletPassword()
        PasswordMatcherValidator(confirmPasswordInputLayout, this::onPasswordChange, password)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, this.theme)
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

        confirmButton.setOnClickListener {
            (activity as? SignInActivity)?.onPasswordConfirmed(currentFragmentTag)
            dismissAllowingStateLoss()
        }
    }

    // MARK - Private Methods

    private fun onPasswordChange() {
        confirmButton.isEnabled = passwordMatcherValidator.isValid()
    }

}