package org.loopring.looprwallet.walletsignin.delegates

import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.walletsignin.models.*
import org.web3j.crypto.Credentials
import org.web3j.utils.Numeric

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class WalletCreationDelegate(
        private val walletName: String,
        private val credentials: Credentials,
        private val password: String?,
        private val keystoreContent: String?,
        private val phrase: Array<String>?,
        private val walletClient: WalletClient
) {

    /**
     * **THIS METHOD BLOCKS AND MUST NOT BE ON THE UI THREAD**
     *
     * Attempts to create a wallet, if possible. Returns a [WalletCreationResult] which wraps the
     * success/failure aspect of the creation.
     *
     * @return An instance of [WalletCreationResult] containing whether or not the operation was
     * successful or the error if the operation was a failure.
     */
    fun createWalletAndBlock(): WalletCreationResult {
        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        return when (walletClient.createWallet(walletName.toLowerCase(), privateKey, keystoreContent, phrase)) {
            true -> when {
                password != null && keystoreContent != null ->
                    WalletCreationKeystore(walletName, password, keystoreContent)

                password != null && phrase != null ->
                    WalletCreationPhrase(walletName, password, ArrayList(phrase.toList()))

                else ->
                    WalletCreationPrivateKey(walletName)
            }
            else -> throw DuplicateWalletException()
        }
    }

}