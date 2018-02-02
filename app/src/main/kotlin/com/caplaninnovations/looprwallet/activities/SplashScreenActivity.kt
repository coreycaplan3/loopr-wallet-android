package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class SplashScreenActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_splash_screen

    override val isSecurityActivity: Boolean
        get() = true

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            val currentWallet = LooprWalletSettings(this).getCurrentWallet()
            if (currentWallet == null) {
                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 1000)
    }

}