package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R

class TransfersActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_transfers

    override val isSecurityActivity: Boolean
        get() = true

}
