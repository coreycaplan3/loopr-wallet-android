package com.caplaninnovations.looprwallet.viewmodels

import android.Manifest
import android.arch.lifecycle.*
import android.os.AsyncTask
import android.support.annotation.RequiresPermission
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.handlers.WalletCreationHandler
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.utilities.FilesUtility
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.str
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

    private val isCreationRunning = MutableLiveData<Boolean>()
    private val isWalletCreated = MutableLiveData<Boolean>()
    private val errorMessage = MutableLiveData<String>()

    fun observeIsCreationRunning(owner: LifecycleOwner, f: (Boolean?) -> Unit) {
        return isCreationRunning.observe(owner, Observer<Boolean> { t -> f.invoke(t) })
    }

    fun observeIsWalletCreated(owner: LifecycleOwner, f: (Boolean?) -> Unit) {
        return isWalletCreated.observe(owner, Observer<Boolean> { t -> f.invoke(t) })
    }

    fun observeErrorMessage(owner: LifecycleOwner, f: (String?) -> Unit) {
        return errorMessage.observe(owner, Observer<String> { t -> f.invoke(t) })
    }

    fun createCredentialsWallet(walletName: String, privateKey: String) {
        WalletCreationAsyncTask(walletName, privateKey, securityClient, this::onCreationPostExecute)
                .execute()
        isCreationRunning.value = true
    }

    fun createKeystoreWallet(walletName: String, password: String, file: File) {
        CreateKeystoreWalletAsyncTask(walletName, password, file, securityClient, this::onCreationPostExecute)
                .execute()
        isCreationRunning.value = true
    }

    fun loadKeystoreWallet(walletName: String, password: String, file: File) {
        LoadKeystoreWalletAsyncTask(walletName, password, file, securityClient, this::onCreationPostExecute)
                .execute()
        isCreationRunning.value = true
    }

    // Mark - Private Methods
    private fun onCreationPostExecute(error: String?) {
        isCreationRunning.value = false

        if (error == null) {
            isWalletCreated.value = true
        } else {
            isWalletCreated.value = false
            errorMessage.value = error
        }
    }

    private open class WalletCreationAsyncTask(
            private val walletName: String,
            private val privateKey: String,
            private val securityClient: SecurityClient,
            private val onComplete: (error: String?) -> Unit
    ) : AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            val credentials = Credentials.create(privateKey)
            return WalletCreationHandler(walletName, credentials, securityClient).createWallet()
        }

        override fun onPostExecute(result: String?) {
            onComplete.invoke(result)
        }

    }

    class CreateKeystoreWalletAsyncTask(
            private val walletName: String,
            private val password: String,
            private val filesDirectory: File,
            private val securityClient: SecurityClient,
            private val onComplete: (error: String?) -> Unit
    ) : AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val generatedWalletName = WalletUtils.generateLightNewWalletFile(password, filesDirectory)
                val credentialFile = File(filesDirectory, FilesUtility.getKeystoreFileName(walletName))

                if (!File(filesDirectory, generatedWalletName).renameTo(credentialFile)) {
                    loge("Could not rename generated wallet filesDirectory!", IllegalStateException())
                    str(R.string.error_creating_wallet)
                } else {
                    val credentials = WalletUtils.loadCredentials(password, credentialFile)
                    WalletCreationHandler(walletName, credentials, securityClient).createWallet()
                }
            } catch (e: Exception) {
                if (e is CipherException) {
                    str(getErrorMessageFromKeystoreError(e))
                } else {
                    loge("Error decrypting wallet!", e)
                    str(R.string.error_unknown)
                }
            }
        }

        override fun onPostExecute(result: String?) {
            onComplete.invoke(result)
        }

    }

    class LoadKeystoreWalletAsyncTask(
            private val walletName: String,
            private val password: String,
            private val file: File,
            private val securityClient: SecurityClient,
            private val onComplete: (error: String?) -> Unit
    ) : AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val credentials = WalletUtils.loadCredentials(password, file)
                WalletCreationHandler(walletName, credentials, securityClient).createWallet()
            } catch (e: Exception) {
                if (e is CipherException) {
                    str(getErrorMessageFromKeystoreError(e))
                } else {
                    loge("Error decrypting wallet!", e)
                    str(R.string.error_unknown)
                }
            }
        }

        override fun onPostExecute(result: String?) {
            onComplete.invoke(result)
        }

    }

}