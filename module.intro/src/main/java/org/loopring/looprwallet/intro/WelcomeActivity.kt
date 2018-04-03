package org.loopring.looprwallet.intro

import com.caplaninnovations.looprwallet.R
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

    override val contentView: Int
        get() = R.layout.activity_welcome

    override val isSecureActivity: Boolean
        get() = false

}