package org.loopring.looprwallet.orderdetails.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.orderdetails.R
import org.loopring.looprwallet.orderdetails.fragments.OrderDetailsFragment

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

        private const val KEY_ORDER_HASH = "_ORDER_HASH"

        /**
         * Routes the current activity to this one
         */
        fun route(activity: Activity, orderHash: String) {
            val intent = Intent(activity, OrderDetailsActivity::class.java)
                    .putExtra(KEY_ORDER_HASH, orderHash)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_order_details

    override val isSignInRequired: Boolean
        get() = true

    private val orderHash: String by lazy {
        intent.getStringExtra(KEY_ORDER_HASH)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val fragment = OrderDetailsFragment.getInstance(orderHash)
            val tag = OrderDetailsFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }
    }

}