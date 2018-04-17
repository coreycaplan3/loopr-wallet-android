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

    override val activityContainerId: Int
        get() = R.id.activityContainer

    override val isSecureActivity: Boolean
        get() = false

}
