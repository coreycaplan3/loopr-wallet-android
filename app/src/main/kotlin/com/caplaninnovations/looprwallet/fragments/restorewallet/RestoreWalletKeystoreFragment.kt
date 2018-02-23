package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_restore_keystore.*
import android.support.design.widget.Snackbar
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.handlers.WalletCreationHandler
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.longToast
import com.caplaninnovations.looprwallet.utilities.snackbar
import com.caplaninnovations.looprwallet.validation.PasswordValidator
import com.caplaninnovations.looprwallet.validation.WalletNameValidator
import kotlinx.android.synthetic.main.card_wallet_name.*
import org.web3j.crypto.CipherException
import org.web3j.crypto.WalletUtils
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

        const val KEY_OPEN_FILE_REQUEST_CODE = 1943934

        const val KEY_URI = "_URI"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_keystore

    private var keystoreUri: Uri? = null
        private set(value) {
            context?.let { keystoreFile = FilesUtility.getFileFromActivityResult(it, value) }
        }


    private var keystoreFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keystoreUri = savedInstanceState?.getParcelable(KEY_URI)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keystoreFile?.let { keystoreSelectFileButton.text = it.name }

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PasswordValidator(keystorePasswordInputLayout, this::onFormChanged)
        )

        keystoreSelectFileButton.setOnClickListener {
            try {
                val intent = FilesUtility.getFileChooserIntent(R.string.title_select_keystore_file)
                startActivityForResult(intent, KEY_OPEN_FILE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                it.snackbar(R.string.error_install_file_manager, Snackbar.LENGTH_INDEFINITE)
            }
        }

        keystoreUnlockButton.setOnClickListener {
            val walletName = walletNameEditText.text.toString()
            val password = keystorePasswordEditText.text.toString()
            val file = keystoreFile

            if (file == null) {
                loge("File was null, this should not have happened!", IllegalStateException())
                it.snackbar(R.string.error_retrieve_file)
                return@setOnClickListener
            }

            try {
                val credentials = WalletUtils.loadCredentials(password, file)

                val securityClient = (activity as? BaseActivity)?.securityClient
                WalletCreationHandler(walletName, credentials, securityClient)
                        .createWallet(view)
            } catch (e: Exception) {
                if (e is CipherException) {
                    it.snackbar(getErrorMessageFromKeystoreError(e))
                    it.snackbar("The given password is incorrect or keystore file is incorrect")

                } else {
                    it.snackbar("There was an error restoring your wallet")
                    loge("Error decrypting wallet!", e)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val context = this.context ?: return

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_URI, keystoreUri)
    }

    override fun onFormChanged() {
        // If all validators are valid and the keystore URI is not null, the user can submit
        keystoreUnlockButton.isEnabled = isAllValidatorsValid() && keystoreFile != null
    }

    // Mark - Private Methods

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    @StringRes
    fun getErrorMessageFromKeystoreError(exception: CipherException): Int {
        val message = exception.message
        return when {
            message == null -> R.string.error_unknown
            message.toLowerCase().contains("supported") -> R.string.error_keystore_unsupported
            message.toLowerCase().contains("password") -> R.string.error_password_invalid
            else -> R.string.error_unknown
        }
    }

}