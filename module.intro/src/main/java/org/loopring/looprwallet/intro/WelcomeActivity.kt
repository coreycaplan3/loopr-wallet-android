package org.loopring.looprwallet.intro

import org.loopring.looprwallet.intro.R
import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey on 1/30/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class WelcomeActivity : BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_welcome

    override val isSignInRequired: Boolean
        get() = false

}