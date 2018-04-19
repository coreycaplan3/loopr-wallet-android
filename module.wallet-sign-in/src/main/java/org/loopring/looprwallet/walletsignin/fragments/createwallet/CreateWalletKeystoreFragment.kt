package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.delegates.PermissionDelegate
import org.loopring.looprwallet.core.utilities.DialogUtility
import org.loopring.looprwallet.core.validators.PasswordValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel.Companion.getMessageFromError

/**
 * Created by Corey Caplan on 2/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateWalletKeystoreFragment : BaseFragment(), ConfirmPasswordDialog.OnPasswordConfirmedListener {

    companion object {
        val TAG: String = CreateWalletKeystoreFragment::class.simpleName!!

        const val KEY_IS_FILE_DIALOG_SHOWING = "_IS_FILE_DIALOG_SHOWING"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_wallet_keystore

    private val filePermissionsHandler by lazy {
        PermissionDelegate(
                activity as BaseActivity,
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionDelegate.REQUEST_CODE_EXTERNAL_FILES,
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
        PasswordValidator(walletPasswordInputLayout, this::onFormChanged)
    }

    private val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(walletNameValidator, passwordValidator)

        createButton.setOnClickListener { onCreateWalletButtonClick() }

        if (savedInstanceState?.getBoolean(KEY_IS_FILE_DIALOG_SHOWING) == true) {
            filePermissionsDialog?.show()
        }

        setupTransactionViewModel(walletGeneratorViewModel, R.string.creating_wallet, ::getMessageFromError)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        logd("Handling file permissions...")
        filePermissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onFormChanged() {
        createButton.isEnabled = isAllValidatorsValid()
    }

    override fun onPasswordConfirmed() {
        val walletName = walletNameEditText.text.toString()
        val password = walletPasswordEditText.text.toString()

        val walletDirectory = view?.context?.filesDir

        if (walletDirectory == null) {
            loge("Wallet directory cannot be null!", IllegalStateException())
            return
        }

        if (!isAllValidatorsValid()) {
            loge("All validators must be valid!", IllegalStateException())
            return
        }

        walletGeneratorViewModel.createKeystoreWallet(walletName, password, walletDirectory)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IS_FILE_DIALOG_SHOWING, filePermissionsDialog?.isShowing == true)
    }

    // Mark - Private Methods

    private fun onCreateWalletButtonClick() {
        if (!isAllValidatorsValid()) {
            loge("Error, this should not have occurred", IllegalStateException())
            return
        }

        if (!filePermissionsHandler.isPermissionGranted) {
            filePermissionsDialog?.show()
        } else {
            onFilePermissionGranted()
        }
    }

    /**
     * Called when permission is granted for the app to access files. This method is *only* called
     * when the rest of the form is valid.
     */
    private fun onFilePermissionGranted() {
        val password = walletPasswordEditText.text.toString()

        ConfirmPasswordDialog.getInstance(TAG, password)
                .show(fragmentManager, ConfirmPasswordDialog.TAG)
    }

    private fun onFilePermissionDenied() {
        view?.snackbar(R.string.permission_rationale_files)
    }

}