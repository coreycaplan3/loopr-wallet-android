package org.loopring.looprwallet.transfer.activities

import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.transfer.R

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class ViewTransfersActivity : BaseActivity() {

    override val contentViewRes: Int
        get() = R.layout.activity_view_transfers

    override val activityContainerId: Int
        get() = R.id.activityContainer

    override val isSecureActivity: Boolean
        get() = true

}
