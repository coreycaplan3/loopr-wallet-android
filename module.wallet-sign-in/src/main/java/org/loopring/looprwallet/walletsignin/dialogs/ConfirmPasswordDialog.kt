package org.loopring.looprwallet.walletsignin.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.os.bundleOf
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.walletsignin.SignInActivity
import org.loopring.looprwallet.walletsignin.models.wallet.PasswordBasedWallet
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationKeystore
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationPhrase
import org.loopring.looprwallet.core.validators.PasswordMatcherValidator
import kotlinx.android.synthetic.main.dialog_confirm_password.*
import org.loopring.looprwallet.core.dialogs.BaseBottomSheetDialog

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A dialog that prompts the user to reenter his/her text password.
 *
 */
class ConfirmPasswordDialog : BaseBottomSheetDialog() {

    /**
     * An interface used to communicate with the activity that a password has been confirmed
     */
    interface OnPasswordConfirmedListener {

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
                arguments = bundleOf(
                        KEY_CURRENT_FRAGMENT_TAG to currentFragmentTag,
                        KEY_WALLET to walletCreationKeystore
                )
            }
        }

        /**
         * @param currentFragmentTag The tag of the currently active fragment. Used to communicate
         * back to it, after the user confirms their password.
         * @param walletCreationPhrase The wallet that the user is creating.
         */
        fun createInstance(currentFragmentTag: String, walletCreationPhrase: WalletCreationPhrase): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = bundleOf(
                        KEY_CURRENT_FRAGMENT_TAG to currentFragmentTag,
                        KEY_WALLET to walletCreationPhrase
                )
            }
        }
    }

    private val currentFragmentTag: String by lazy {
        arguments!!.getString(KEY_CURRENT_FRAGMENT_TAG)
    }

    private val passwordBasedWallet: PasswordBasedWallet by lazy {
        arguments!!.getParcelable<Parcelable>(KEY_WALLET) as PasswordBasedWallet
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirm_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val correctPassword = passwordBasedWallet.getWalletPassword()
        validatorList = listOf(
                PasswordMatcherValidator(confirmPasswordInputLayout, correctPassword, this::onFormChanged)
        )

        confirmButton.setOnClickListener {
            (activity as? SignInActivity)?.onPasswordConfirmed(currentFragmentTag)
            dismissAllowingStateLoss()
        }
    }

    override fun onFormChanged() {
        confirmButton.isEnabled = isAllValidatorsValid()
    }

}