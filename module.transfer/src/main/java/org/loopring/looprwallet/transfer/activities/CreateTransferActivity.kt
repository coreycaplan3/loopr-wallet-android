package org.loopring.looprwallet.transfer.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.transfer.R
import org.loopring.looprwallet.transfer.fragments.SelectTransferContactFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateTransferActivity : BaseActivity() {

    companion object {

        const val RC_CREATE_TRANSFER = 132

        fun route(activity: Activity) {
            val intent = Intent(activity, CreateTransferActivity::class.java)
            activity.startActivityForResult(intent, RC_CREATE_TRANSFER)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_transfer

    override val activityContainerId: Int
        get() = R.id.activityContainer

    override val isSecureActivity: Boolean
        get() = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(SelectTransferContactFragment(), SelectTransferContactFragment.TAG)
        }
    }

}