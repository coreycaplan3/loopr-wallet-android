package org.loopring.looprwallet.walletsignin.dialogs

import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.activities.SignInActivity
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

        const val KEY_PASSWORD = "_PASSWORD"

        /**
         * @param currentFragmentTag The tag of the currently active fragment. Used to communicate
         * back to it, after the user confirms their password.
         * @param password The correct password for the wallet.
         */
        fun getInstance(currentFragmentTag: String, password: String): ConfirmPasswordDialog {
            return ConfirmPasswordDialog().apply {
                arguments = bundleOf(
                        KEY_CURRENT_FRAGMENT_TAG to currentFragmentTag,
                        KEY_PASSWORD to password
                )
            }
        }

    }

    override val layoutResource: Int
        get() = R.layout.dialog_confirm_password

    private val currentFragmentTag: String by lazy {
        arguments!!.getString(KEY_CURRENT_FRAGMENT_TAG)
    }

    private val password: String by lazy {
        arguments!!.getString(KEY_PASSWORD)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(
                PasswordMatcherValidator(confirmPasswordInputLayout, password, this::onFormChanged)
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