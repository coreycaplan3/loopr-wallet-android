package org.loopring.looprwallet.createorder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.fragments.CreateOrderFragment
import org.loopring.looprwallet.createorder.fragments.OrderPlacedFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateOrderActivity : BaseActivity() {

    companion object {

        private const val KEY_MARKET = "MARKET"
        private const val KEY_IS_SELL = "IS_SELL"

        /**
         * Routes this activity to the [CreateOrderActivity].
         *
         * @param tradingPair The [TradingPair] that will be selected by default
         */
        fun route(activity: Activity, tradingPair: TradingPair? = null, isSell: Boolean = false) {
            val intent = Intent(activity, CreateOrderActivity::class.java)
                    .putExtra(KEY_MARKET, tradingPair?.market)
                    .putExtra(KEY_IS_SELL, isSell)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_order

    override val isSignInRequired = true

    private val market: String? by lazy {
        intent.getStringExtra(KEY_MARKET)
    }

    private val isSell: Boolean by lazy {
        intent.getBooleanExtra(KEY_IS_SELL, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val fragment = CreateOrderFragment.getInstance(isSell, market)
            val tag = CreateOrderFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(OrderPlacedFragment.TAG)
        if (fragment is OrderPlacedFragment) {
            // You cannot go back after placing an order
            finish()
        } else {
            super.onBackPressed()
        }
    }

}