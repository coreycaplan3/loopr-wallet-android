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
        activity: Activity?
) {

    private val securityClient: SecurityClient? = (activity as? BaseActivity)?.securityClient

    /**
     * Attempts to create a wallet, if possible, and then goes to the [MainActivity] with the newly
     * signed-in wallet.
     *
     * @param view A view used for creating a snackbar, for when the creation succeeds or fails
     */
    fun createWallet(view: View) {
        if (securityClient == null) {
            loge("Could not create wallet!", IllegalStateException())
            view.snackbar(R.string.error_creating_wallet)
            return
        }

        val privateKey = Numeric.encodeQuantity(credentials.ecKeyPair.privateKey)

        when (securityClient.createWallet(walletName, privateKey)) {
            true -> {
                view.snackbar(R.string.wallet_creation_successful)

                val intent = Intent(view.context, MainActivity::class.java)
                        .addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)

                view.context.startActivity(intent)
            }
            false -> {
                view.snackbar(R.string.error_wallet_already_exists)
            }
        }
    }

}