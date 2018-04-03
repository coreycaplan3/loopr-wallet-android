package org.loopring.looprwallet.transfer

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
class ViewTransfersActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_view_transfers

    override val isSecureActivity: Boolean
        get() = true

}
