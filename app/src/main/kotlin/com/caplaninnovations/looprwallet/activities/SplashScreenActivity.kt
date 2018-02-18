package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Handler
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.longToast

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

    override val isSecurityActivity: Boolean
        get() = true

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            if (securityClient.isAndroidKeystoreUnlocked()) {
                val intent =
                        if (securityClient.getCurrentWallet() == null) {
                            Intent(this, SignInActivity::class.java)
                        } else {
                            Intent(this, MainActivity::class.java)
                        }

                startActivity(intent)
                finish()
            } else {
                longToast(R.string.unlock_device
                )
                securityClient.unlockAndroidKeystore()
            }
        }, 1000)
    }

}