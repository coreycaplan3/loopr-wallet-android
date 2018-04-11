package org.loopring.looprwallet.walletsignin.delegates

import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationResult
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
class WalletCreationDelegates(
        private val walletName: String,
        private val credentials: Credentials,
        private val walletClient: WalletClient?
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
    fun createWallet(): WalletCreationResult {
        if (walletClient == null) {
            loge("Could not factory currentWallet!", IllegalStateException())
            return WalletCreationResult(false, str(R.string.error_creating_wallet))
        }

        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        return when (walletClient.createWallet(walletName.toLowerCase(), privateKey)) {
            true -> WalletCreationResult(true, null)
            else -> WalletCreationResult(false, str(R.string.error_wallet_already_exists))
        }
    }

}