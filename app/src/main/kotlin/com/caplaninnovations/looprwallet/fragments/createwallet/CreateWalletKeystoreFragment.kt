package com.caplaninnovations.looprwallet.fragments.createwallet

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.dialogs.ConfirmPasswordDialog
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.PermissionHandler
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore
import com.caplaninnovations.looprwallet.utilities.DialogUtility
import com.caplaninnovations.looprwallet.utilities.allNonnull
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.snackbar
import com.caplaninnovations.looprwallet.validators.PasswordValidator
import com.caplaninnovations.looprwallet.validators.WalletNameValidator
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.card_enter_wallet_password.*

/**
 * Created by Corey Caplan on 2/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateWalletKeystoreFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateWalletKeystoreFragment::class.simpleName!!

        const val KEY_IS_FILE_DIALOG_SHOWING = "_IS_FILE_DIALOG_SHOWING"
        const val KEY_WALLET_CREATION_KEYSTORE = "_WALLET_CREATION_KEYSTORE"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_wallet_keystore

    var walletCreationKeystore: WalletCreationKeystore? = null

    private val filePermissionsHandler by lazy {
        PermissionHandler(
                activity as BaseActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionHandler.REQUEST_CODE_EXTERNAL_FILES,
                this::onFilePermissionGranted,
                this::onFilePermissionDenied
        )
    }

    private val filePermissionsDialog: AlertDialog? by lazy {
        context?.let {
            DialogUtility.createFilePermissionsDialog(it, filePermissionsHandler)
        }
    }

    private val walletNameValidator: WalletNameValidator by lazy {
        WalletNameValidator(walletNameInputLayout, this::onFormChanged)
    }

    private val passwordValidator: PasswordValidator by lazy {
        PasswordValidator(enterWalletPasswordInputLayout, this::onFormChanged)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        walletCreationKeystore = savedInstanceState?.getParcelable(KEY_WALLET_CREATION_KEYSTORE)

        validatorList = listOf(walletNameValidator, passwordValidator)

        createButton.setOnClickListener(this::onCreateWalletButtonClick)

        if(savedInstanceState?.getBoolean(KEY_IS_FILE_DIALOG_SHOWING) == true) {
            filePermissionsDialog?.show()
        }
    }

    override fun onFormChanged() {
        createButton.isEnabled = isAllValidatorsValid()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_WALLET_CREATION_KEYSTORE, walletCreationKeystore)
        outState.putBoolean(KEY_IS_FILE_DIALOG_SHOWING, filePermissionsDialog?.isShowing == true)
    }

    // Mark - Private Methods

    private fun onCreateWalletButtonClick(buttonView: View) {
        if (isAllValidatorsValid()) {
            val walletName = walletNameValidator.getText()
            val password = passwordValidator.getText()

            if (!listOf(walletName, password).allNonnull()) {
                loge("Error! This should not have occurred", IllegalStateException())
                buttonView.snackbar(R.string.error_unknown)
                return
            }

            walletCreationKeystore = WalletCreationKeystore(walletName!!, password!!)

            if (!filePermissionsHandler.isPermissionGranted) {
                filePermissionsDialog?.show()
            }
        }
    }

    /**
     * Called when permission is granted for the app to access files. This method is *only* called
     * when the rest of the form is valid.
     */
    private fun onFilePermissionGranted() {
        val wallet = walletCreationKeystore
        if (wallet != null) {
            ConfirmPasswordDialog.createInstance(wallet)
                    .show(fragmentManager, ConfirmPasswordDialog.TAG)
        } else {
            loge("Error invalid state!", IllegalStateException())
        }
    }

    private fun onFilePermissionDenied() {
        view?.snackbar(R.string.permission_rationale_files)
    }

}