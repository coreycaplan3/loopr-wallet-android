package org.loopring.looprwallet.intro

import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class IntroActivity : BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_intro

    override val isSignInRequired: Boolean
        get() = false

}
