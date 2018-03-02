package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.*
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.handlers.WalletCreationHandler
import com.caplaninnovations.looprwallet.models.android.observers.observeForDoubleSpend
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationResult
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.snackbar
import com.caplaninnovations.looprwallet.utilities.str
import kotlinx.coroutines.experimental.async
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.io.File

/**
 * Created by Corey on 2/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class WalletGeneratorViewModel : ViewModel() {

    companion object {

        /**
         * Setups and standardizes the usage of [WalletGeneratorViewModel]. This includes the observers
         * used to watch for wallet generation.
         *
         * @param walletGeneratorViewModel The generator used to create/restore wallets
         * @param fragment The fragment in which this view model resides
         */
        fun setupForFragment(walletGeneratorViewModel: WalletGeneratorViewModel,
                             fragment: Fragment) {
            val activity = fragment.activity
            walletGeneratorViewModel.isCreationRunning.observeForDoubleSpend(fragment, {
                val progress = (activity as? BaseActivity)?.progressDialog

                if (it) {
                    progress?.setMessage(str(R.string.creating_wallet))
                    progress?.show()
                } else {
                    if (progress?.isShowing == true) {
                        progress.dismiss()
                    }
                }
            })

            walletGeneratorViewModel.walletCreation.observeForDoubleSpend(fragment, {
                if (it.isSuccessful) {
                    activity?.startActivity(MainActivity.createIntent())
                    activity?.finish()
                } else {
                    it.error?.let { fragment.view?.snackbar(it) }
                }
            })

        }

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

    private val securityClient = LooprWalletApp.application.securityClient

    /**
     * Tracks whether or not wallet creation is running.
     */
    val isCreationRunning = MutableLiveData<Boolean>()

    /**
     * Tracks whether or not the wallet was created, if it was successful, and any possible errors
     * with its creation
     */
    val walletCreation = MutableLiveData<WalletCreationResult>()

    fun createCredentialsWallet(walletName: String, privateKey: String) = createWalletAsync {
        val credentials = Credentials.create(privateKey)
        WalletCreationHandler(walletName, credentials, securityClient).createWallet()
    }

    /**
     * Attempts to create a wallet using a password and keystore.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     * @param filesDirectory The directory in which the keystore can be generated.
     */
    fun createKeystoreWallet(walletName: String, password: String, filesDirectory: File) = createWalletAsync {
        try {
            val generatedWalletName = WalletUtils.generateLightNewWalletFile(password, filesDirectory)
            val generatedFile = File(filesDirectory, generatedWalletName)
            val credentialFile = File(filesDirectory, FilesUtility.getKeystoreFileName(walletName))

            when (generatedFile.renameTo(credentialFile)) {
                true -> {
                    loge("Could not rename generated wallet file in filesDirectory!", IllegalStateException())
                    WalletCreationResult(false, str(R.string.error_creating_wallet))
                }
                false -> {
                    val credentials = WalletUtils.loadCredentials(password, credentialFile)
                    WalletCreationHandler(walletName, credentials, securityClient).createWallet()
                }
            }
        } catch (e: Exception) {
            if (e is CipherException) {
                WalletCreationResult(false, str(getErrorMessageFromKeystoreError(e)))
            } else {
                loge("Error decrypting wallet!", e)
                WalletCreationResult(false, str(R.string.error_unknown))
            }
        }
    }

    fun loadKeystoreWallet(walletName: String, password: String, file: File) = createWalletAsync {
        try {
            val credentials = WalletUtils.loadCredentials(password, file)
            WalletCreationHandler(walletName, credentials, securityClient).createWallet()
        } catch (e: Exception) {
            if (e is CipherException) {
                WalletCreationResult(false, str(getErrorMessageFromKeystoreError(e)))
            } else {
                loge("Error decrypting wallet!", e)
                WalletCreationResult(false, str(R.string.error_unknown))
            }
        }
    }


    // Mark - Private Methods

    /**
     * Creates or restores a wallet async (non-blocking) and calls
     * @param block A function that takes no parameters and returns a [WalletCreationResult]
     */
    private fun createWalletAsync(block: () -> WalletCreationResult) {
        async {
            isCreationRunning.value = true
            val walletCreationResult = block.invoke()
            isCreationRunning.value = false
            walletCreation.value = walletCreationResult
        }
    }

}