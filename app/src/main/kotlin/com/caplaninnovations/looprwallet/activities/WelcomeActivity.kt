package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R

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

    override val isSecurityActivity: Boolean
        get() = false

}