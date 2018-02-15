package com.caplaninnovations.looprwallet.activities

import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class TransfersActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_view_transfers

    override val isSecurityActivity: Boolean
        get() = true

}
