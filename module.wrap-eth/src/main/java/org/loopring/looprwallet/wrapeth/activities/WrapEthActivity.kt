package org.loopring.looprwallet.wrapeth.activities

import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.wrapeth.R

/**
 * Created by Corey on 4/30/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class WrapEthActivity : BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_wrap_eth

    override val isSignInRequired: Boolean
        get() = true

}