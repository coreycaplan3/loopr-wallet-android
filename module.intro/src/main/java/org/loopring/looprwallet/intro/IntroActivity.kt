package org.loopring.looprwallet.intro

import org.loopring.looprwallet.core.R
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

    override val contentView: Int
        get() = R.layout.activity_intro

    override val isSecureActivity: Boolean
        get() = false

}
