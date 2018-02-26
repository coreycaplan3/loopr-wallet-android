package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.models.security.SecurityClient
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
     * Attempts to create a wallet, if possible, and then goes to the [MainActivity] with the newly
     * signed-in wallet.
     *
     * @return A string containing the error if the operation was a failure or null if it was a
     * success
     */
    fun createWallet(): String? {
        if (securityClient == null) {
            loge("Could not create wallet!", IllegalStateException())
            return str(R.string.error_creating_wallet)
        }

        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        return when (securityClient.createWallet(walletName, privateKey)) {
            true -> null
            false -> str(R.string.error_wallet_already_exists)
        }
    }

}