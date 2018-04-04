package org.loopring.looprwallet.walletsignin.fragments.restorewallet

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_restore_keystore.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.extensions.snackbarWithAction
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.handlers.PermissionHandler
import org.loopring.looprwallet.core.utilities.DialogUtility
import org.loopring.looprwallet.core.utilities.FilesUtility
import org.loopring.looprwallet.core.validators.PasswordValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel
import java.io.File

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class RestoreWalletKeystoreFragment : BaseFragment() {

    companion object {
        val TAG: String = RestoreWalletKeystoreFragment::class.java.simpleName

        const val KEY_OPEN_FILE_REQUEST_CODE = 194

        const val KEY_IS_FILE_DIALOG_SHOWING = "_IS_FILE_DIALOG_SHOWING"
        const val KEY_URI = "_URI"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_keystore

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

    val filePermissionsDialog: AlertDialog? by lazy {
        context?.let {
            DialogUtility.createFilePermissionsDialog(it, filePermissionsHandler)
        }
    }

    var keystoreUri: Uri? = null
        set(value) {
            field = value

            val context = this.context
            if (context == null) {
                loge("Context was null!", IllegalStateException())
            } else {
                keystoreFile = FilesUtility.getFileFromUri(context, value)
            }
        }

    var keystoreFile: File? = null
        private set(value) {
            field = value

            val name = value?.name
            when {
                name != null -> {
                    keystoreSelectFileButton.text = name
                }
                else -> {
                    keystoreSelectFileButton.setText(R.string.select_keystore_file)
                }
            }

            // We need to manually propagate form changes of the file
            onFormChanged()
        }

    private val walletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    // END PROPERTIES

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keystoreUri = savedInstanceState?.getParcelable(KEY_URI)

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PasswordValidator(keystorePasswordInputLayout, this::onFormChanged)
        )

        if (savedInstanceState?.getBoolean(KEY_IS_FILE_DIALOG_SHOWING) == true) {
            filePermissionsDialog?.show()
        }

        keystoreSelectFileButton.setOnClickListener {
            try {
                val intent = FilesUtility.getFileChooserIntent(R.string.title_select_keystore_file)
                startActivityForResult(intent, KEY_OPEN_FILE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                it.snackbar(R.string.error_install_file_manager, Snackbar.LENGTH_INDEFINITE)
            }
        }

        keystoreUnlockButton.setOnClickListener(this::onUnlockButtonClick)

        WalletGeneratorViewModel.setupForFragment(walletGeneratorViewModel, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        val context = context
        if (context == null) {
            loge("Context was null!", IllegalStateException())
            return
        }

        if (requestCode == KEY_OPEN_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent == null) {
                loge("Invalid state, how did this happen?", IllegalStateException())
                context.longToast(R.string.error_retrieve_file)
                return
            }

            keystoreUri = intent.data
            onFormChanged()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        filePermissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onFormChanged() {
        // If all validators are valid and the keystore URI is not null, the user can submit
        keystoreUnlockButton.isEnabled = isAllValidatorsValid() && keystoreFile != null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_URI, keystoreUri)
        outState.putBoolean(KEY_IS_FILE_DIALOG_SHOWING, filePermissionsDialog?.isShowing == true)

        filePermissionsDialog?.dismiss()
    }

    // Mark - Private Methods

    /**
     * Called when permission is granted for the app to access files. This method is *only* called
     * when the rest of the form is valid.
     */
    private fun onFilePermissionGranted() {
        if (keystoreUnlockButton.isEnabled) {
            keystoreUnlockButton.performClick()
        } else {
            loge("Error invalid state!", IllegalStateException())
            view?.snackbar(R.string.you_may_finish_restoring_wallet)
        }
    }

    private fun onFilePermissionDenied() {
        view?.snackbarWithAction(
                R.string.permission_rationale_files,
                Snackbar.LENGTH_INDEFINITE,
                R.string.allow) { _ -> filePermissionsHandler.requestPermission() }
    }

    private fun onUnlockButtonClick(buttonView: View) {
        if (!filePermissionsHandler.isPermissionGranted) {
            filePermissionsDialog?.show()
            return
        }

        val walletName = walletNameEditText.text.toString()
        val password = keystorePasswordEditText.text.toString()
        val file = keystoreFile

        if (file == null) {
            loge("File was null, this should not have happened!", IllegalStateException())
            buttonView.snackbar(R.string.error_retrieve_file)
            return
        }

        walletGeneratorViewModel.loadKeystoreWallet(walletName, password, file)
    }

}