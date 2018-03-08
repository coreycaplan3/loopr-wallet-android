package com.caplaninnovations.looprwallet.handlers

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationResult
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.str
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
class WalletCreationHandler(
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
            loge("Could not create wallet!", IllegalStateException())
            return WalletCreationResult(false, str(R.string.error_creating_wallet))
        }

        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        return when (walletClient.createWallet(walletName.toLowerCase(), privateKey)) {
            true -> WalletCreationResult(true, null)
            false -> WalletCreationResult(false, str(R.string.error_wallet_already_exists))
        }
    }

}