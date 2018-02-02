package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R

class IntroActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_intro

    override val isSecurityActivity: Boolean
        get() = false

}
