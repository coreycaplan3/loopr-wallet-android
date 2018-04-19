package org.loopring.looprwallet.walletsignin.viewmodels

import kotlinx.coroutines.experimental.async
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.toArrayList
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.FilesUtility
import org.loopring.looprwallet.core.utilities.RegexUtility
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.dagger.walletLooprComponent
import org.loopring.looprwallet.walletsignin.delegates.WalletCreationDelegate
import org.loopring.looprwallet.walletsignin.models.DuplicateWalletException
import org.loopring.looprwallet.walletsignin.models.WalletCreationPhrase
import org.loopring.looprwallet.walletsignin.models.WalletCreationResult
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import javax.inject.Inject

/**
 * Created by Corey on 2/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class WalletGeneratorViewModel : TransactionViewModel<WalletCreationResult>() {

    companion object {

        fun getMessageFromError(exception: Throwable): String {
            val message = exception.message
            val stringRes = when {
                exception is DuplicateWalletException -> R.string.error_wallet_already_exists
                message == null -> R.string.error_unknown
                message.toLowerCase().contains("supported") -> R.string.error_keystore_unsupported
                message.toLowerCase().contains("password") -> R.string.error_password_invalid
                else -> R.string.error_unknown
            }

            return str(stringRes)
        }

    }

    @Inject
    lateinit var walletClient: WalletClient

    init {
        walletLooprComponent.inject(this)
    }

    fun createPrivateKeyWallet(walletName: String, privateKey: String) = createWalletAsync {
        val credentials = Credentials.create(privateKey)
        val delegate = WalletCreationDelegate(
                walletName = walletName,
                credentials = credentials,
                password = null,
                keystoreContent = null,
                phrase = null,
                walletClient = walletClient
        )

        return@createWalletAsync delegate.createWalletAndBlock()
    }

    /**
     * Attempts to create a wallet using a password and keystore.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     * @param filesDirectory The directory in which the phrase can be generated.
     */
    fun createKeystoreWallet(walletName: String, password: String, filesDirectory: File) = createWalletAsync {
        val filename = WalletUtils.generateLightNewWalletFile(password, filesDirectory)
        val generatedFile = File(filesDirectory, filename)
        val credentialFile = File(filesDirectory, FilesUtility.getWalletFilename(walletName))

        when (generatedFile.renameTo(credentialFile)) {
            true -> {
                val credentials = WalletUtils.loadCredentials(password, credentialFile)
                val keystoreContent = credentialFile.readText()
                val delegate = WalletCreationDelegate(
                        walletName = walletName,
                        credentials = credentials,
                        password = password,
                        keystoreContent = keystoreContent,
                        phrase = null,
                        walletClient = walletClient
                )
                return@createWalletAsync delegate.createWalletAndBlock()
            }
            false -> {
                throw IOException("Could not rename generated wallet file in filesDirectory!")
            }
        }

    }

    fun loadKeystoreWallet(walletName: String, password: String, file: File) = createWalletAsync {
        val credentials = WalletUtils.loadCredentials(password, file)
        val keystoreContent = file.readText()
        val delegate = WalletCreationDelegate(
                walletName = walletName,
                credentials = credentials,
                password = password,
                keystoreContent = keystoreContent,
                phrase = null,
                walletClient = walletClient
        )
        return@createWalletAsync delegate.createWalletAndBlock()
    }

    /**
     * Attempts to create a 12-word phrase for a Bip39 wallet.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     */
    fun createPhraseAsync(walletName: String, password: String) = async {
        populateWordListFromAssets()

        val initialEntropy = ByteArray(16)
        SecureRandom().nextBytes(initialEntropy)

        val phrase = MnemonicUtils.generateMnemonic(initialEntropy)
        val phraseList = phrase.split(RegexUtility.WHITESPACE_REGEX).toArrayList()
        mResult.postValue(WalletCreationPhrase(walletName, password, phraseList))
    }

    /**
     * Attempts to create a wallet using a password and keystore.
     *
     * @param walletName The wallet's *unique* name
     * @param password The wallet's password, used to derive the private key.
     * @param phrase The 12-word phrase used to recover the wallet and derive the private key
     */
    fun loadPhraseWallet(walletName: String, password: String, phrase: ArrayList<String>) = createWalletAsync {
        val phraseAsString = StringBuilder()
        phrase.forEachIndexed { index, item ->
            phraseAsString.append(item)

            val isNotLastIndex = index < phrase.size - 1
            if (isNotLastIndex) {
                phraseAsString.append(" ")
            }
        }

        val credentials = WalletUtils.loadBip39Credentials(password, phraseAsString.toString())
        val delegate = WalletCreationDelegate(
                walletName = walletName,
                credentials = credentials,
                password = password,
                keystoreContent = null,
                phrase = phrase.toTypedArray(),
                walletClient = walletClient
        )
        return@createWalletAsync delegate.createWalletAndBlock()
    }

    // Mark - Private Methods

    /**
     * Creates or restores a wallet async (non-blocking) and calls
     * @param block A function that takes no parameters and returns a [WalletCreationResult]
     */
    private inline fun createWalletAsync(crossinline block: () -> WalletCreationResult) = async {
        mIsTransactionRunning.postValue(true)

        var walletCreationResult: WalletCreationResult? = null
        var creationError: Throwable? = null

        try {
            walletCreationResult = block.invoke()
        } catch (e: Throwable) {
            loge("Error: ", e)
            creationError = e
        }
        mIsTransactionRunning.postValue(false)

        walletCreationResult?.let { mResult.postValue(it) }
        creationError?.let { mError.postValue(it) }

        Unit
    }

    /**
     * A work-around because the default population of words **FAILS** in [MnemonicUtils] due to it
     * not being Android compatible
     */
    private fun populateWordListFromAssets() {
        val stream = CoreLooprWalletApp.context.assets.open("en-mnemonic-word-list.txt")
        val lines = stream.bufferedReader().readLines()

        val field = MnemonicUtils::class.java.getDeclaredField("WORD_LIST")
        field.isAccessible = true

        field.set(null, lines)
    }

}