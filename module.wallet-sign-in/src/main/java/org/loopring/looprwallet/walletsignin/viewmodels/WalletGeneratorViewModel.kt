package org.loopring.looprwallet.walletsignin.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.extensions.toArrayList
import org.loopring.looprwallet.walletsignin.handlers.WalletCreationHandler
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationPhrase
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationResult
import org.loopring.looprwallet.core.utilities.*
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import kotlinx.coroutines.experimental.async
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.io.File
import java.security.SecureRandom

/**
 * Created by Corey on 2/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
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

    private val securityClient = LooprWalletApp.application.walletClient

    /**
     * Tracks whether or not wallet creation is running.
     */
    val isCreationRunning = MutableLiveData<Boolean>()

    /**
     * Tracks whether or not the wallet was created, if it was successful, and any possible errors
     * with its creation
     */
    val walletCreation = MutableLiveData<WalletCreationResult>()

    val walletPhraseGeneration = MutableLiveData<WalletCreationPhrase>()

    fun createCredentialsWallet(walletName: String, privateKey: String) = createWalletAsync {
        val credentials = Credentials.create(privateKey)
        WalletCreationHandler(walletName, credentials, securityClient).createWallet()
    }

    /**
     * Attempts to create a wallet using a password and keystore.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     * @param filesDirectory The directory in which the phrase can be generated.
     */
    fun createKeystoreWallet(walletName: String, password: String, filesDirectory: File) = createWalletAsync {
        try {
            val filename = WalletUtils.generateLightNewWalletFile(password, filesDirectory)
            val generatedFile = File(filesDirectory, filename)
            val credentialFile = File(filesDirectory, FilesUtility.getWalletFilename(walletName))

            when (generatedFile.renameTo(credentialFile)) {
                true -> {
                    val credentials = WalletUtils.loadCredentials(password, credentialFile)
                    WalletCreationHandler(walletName, credentials, securityClient).createWallet()
                }
                false -> {
                    loge("Could not rename generated currentWallet file in filesDirectory!", IllegalStateException())
                    WalletCreationResult(false, str(R.string.error_creating_wallet))
                }
            }
        } catch (e: Exception) {
            if (e is CipherException) {
                WalletCreationResult(false, str(getErrorMessageFromKeystoreError(e)))
            } else {
                loge("Error decrypting currentWallet!", e)
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
                loge("Error decrypting currentWallet!", e)
                WalletCreationResult(false, str(R.string.error_unknown))
            }
        }
    }

    /**
     * Attempts to create a 12-word phrase for a Bip39 wallet.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     */
    fun createPhraseAsync(walletName: String, password: String) {
        async {
            try {
                populateWordListFromAssets()

                val initialEntropy = ByteArray(16)
                SecureRandom().nextBytes(initialEntropy)

                val phrase = MnemonicUtils.generateMnemonic(initialEntropy)
                val phraseList = phrase.split(RegexUtility.WHITESPACE_REGEX).toArrayList()
                walletCreation.postValue(WalletCreationResult(true, null))
                walletPhraseGeneration.postValue(WalletCreationPhrase(walletName, password, phraseList))
            } catch (e: Exception) {
                loge("Could not factory phrase: ", e)
                val error = str(R.string.error_creating_wallet)
                walletCreation.postValue(WalletCreationResult(false, error))
            }
        }
    }

    /**
     * Attempts to create a wallet using a password and keystore.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     * @param phrase The 12-word phrase used to recover the wallet and derive the private key
     */
    fun loadPhraseWallet(walletName: String, password: String, phrase: ArrayList<String>) = createWalletAsync {
        try {
            val phraseAsString = StringBuilder()
            phrase.forEachIndexed { index, item ->
                phraseAsString.append(item)

                val isNotLastIndex = index < phrase.size - 1
                if (isNotLastIndex) {
                    phraseAsString.append(" ")
                }
            }

            val credentials = WalletUtils.loadBip39Credentials(password, phraseAsString.toString())
            WalletCreationHandler(walletName, credentials, securityClient).createWallet()
        } catch (e: Exception) {
            if (e is CipherException) {
                WalletCreationResult(false, str(getErrorMessageFromKeystoreError(e)))
            } else {
                loge("Error decrypting currentWallet!", e)
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
            isCreationRunning.postValue(true)
            val walletCreationResult = block.invoke()
            isCreationRunning.postValue(false)
            walletCreation.postValue(walletCreationResult)
        }
    }

    /**
     * A work-around because the default population of words **FAILS** in [MnemonicUtils] due to it
     * not being Android compatible
     */
    private fun populateWordListFromAssets() {
        val stream = LooprWalletApp.context.assets.open("en-mnemonic-word-list.txt")
        val lines = stream.bufferedReader().readLines()

        val field = MnemonicUtils::class.java.getDeclaredField("WORD_LIST")
        field.isAccessible = true

        field.set(null, lines)
    }

}