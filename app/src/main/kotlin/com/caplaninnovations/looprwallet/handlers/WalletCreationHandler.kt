package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationResult
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.snackbar
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
        private val securityClient: SecurityClient?
) {

    /**
     * Attempts to create a wallet, if possible. Return s
     *
     * @return An instance of [WalletCreationResult] containing whether or not the operation was
     * successful or the error if the operation was a failure.
     */
    fun createWallet(): WalletCreationResult {
        if (securityClient == null) {
            loge("Could not create wallet!", IllegalStateException())
            return WalletCreationResult(false, str(R.string.error_creating_wallet))
        }

        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        return when (securityClient.createWallet(walletName, privateKey)) {
            true -> WalletCreationResult(true, null)
            false -> WalletCreationResult(false, str(R.string.error_wallet_already_exists))
        }
    }

}