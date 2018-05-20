package org.loopring.looprwallet.activities

import android.content.Intent
import org.loopring.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.home.activities.MainActivity
import org.loopring.looprwallet.walletsignin.activities.SignInActivity

/**
 * Created by Corey Caplan on 2/1/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class InitialRoutingActivity : BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_splash

    override val isSignInRequired: Boolean
        get() = false

    override fun onResume() {
        super.onResume()

        if (walletClient.isAndroidKeystoreUnlocked()) {

            if (BuildUtility.BUILD_TYPE == BuildUtility.BUILD_DEBUG) {
                walletClient.createWallet("loopr", "0321321123312132321312321312123312312123132321213212323112331232", null, null)
            }

            when {
                walletClient.getCurrentWallet() == null -> SignInActivity.route(this, false)
                else -> MainActivity.routeAndClearOldTasks(this)
            }
            finish()
        } else {
            walletClient.unlockAndroidKeystore()
        }
    }

}