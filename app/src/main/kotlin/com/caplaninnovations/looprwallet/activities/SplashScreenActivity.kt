package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 2/1/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class SplashScreenActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_splash

    override val isSecureActivity: Boolean
        get() = false

    override fun onResume() {
        super.onResume()

        if (walletClient.isAndroidKeystoreUnlocked()) {
            val intent = if (walletClient.getCurrentWallet() == null) {
                Intent(this, SignInActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
                        .putExtra("AddFragment", true)
            }

            startActivity(intent)
            finish()
        } else {
            walletClient.unlockAndroidKeystore()
        }
    }

}