package com.caplaninnovations.looprwallet.activities

import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class SearchActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_search

    override val isSecureActivity: Boolean
        get() = true

}
