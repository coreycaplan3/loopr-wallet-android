package org.loopring.looprwallet.orderdetails.activities

import android.app.Activity
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.orderdetails.R

/**
 * Created by Corey Caplan on 4/29/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class OrderDetailsActivity : BaseActivity() {

    companion object {

        /**
         * Routes the current activity to this one
         */
        fun route(activity: Activity, orderHash: String) {
            // TODO
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_order_details

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // TODO
        }
    }

}