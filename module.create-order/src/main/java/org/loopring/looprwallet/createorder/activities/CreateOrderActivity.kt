package org.loopring.looprwallet.createorder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.createorder.R
import org.loopring.looprwallet.createorder.fragments.CreateOrderFragment

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

        /**
         * Routes this activity to the [CreateOrderActivity].
         *
         * @param tradingPair The [TradingPair] that will be selected by default
         */
        fun route(activity: Activity, tradingPair: TradingPair? = null) {
            val intent = Intent(activity, CreateOrderActivity::class.java)
                    .putExtra(KEY_MARKET, tradingPair?.market)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_order

    override val isSignInRequired = true

    private val market: String? by lazy {
        intent.getStringExtra(KEY_MARKET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val fragment = CreateOrderFragment.getInstance(market)
            val tag = CreateOrderFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }
    }

}