package org.loopring.looprwallet.walletsignin.fragments

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.handlers.PermissionHandler
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationKeystore
import org.loopring.looprwallet.core.utilities.DialogUtility
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.validators.PasswordValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import com.caplaninnovations.looprwallet.viewmodels.wallet.WalletGeneratorViewModel

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
        PermissionHandler(
                activity as BaseActivity,
                this,
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

        WalletGeneratorViewModel.setupForFragment(walletGeneratorViewModel, this)
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
        val walletName = walletNameEditText.text.toString()
        val password = walletPasswordEditText.text.toString()

        val wallet = WalletCreationKeystore(walletName, password)

        val dialog = ConfirmPasswordDialog.createInstance(TAG, wallet)
        dialog.show(fragmentManager, ConfirmPasswordDialog.TAG)
    }

    private fun onFilePermissionDenied() {
        view?.snackbar(R.string.permission_rationale_files)
    }

}