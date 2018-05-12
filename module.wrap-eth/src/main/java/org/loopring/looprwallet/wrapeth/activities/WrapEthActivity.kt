package org.loopring.looprwallet.wrapeth.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.wrapeth.R
import org.loopring.looprwallet.wrapeth.fragments.WrapEthFragment

/**
 * Created by Corey on 4/30/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class WrapEthActivity : BaseActivity() {

    companion object {

        fun route(activity: Activity) {
            activity.startActivity(Intent(activity, WrapEthActivity::class.java))
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_wrap_eth

    override val isSignInRequired: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(WrapEthFragment(), WrapEthFragment.TAG)
        }
    }

}