package org.loopring.looprwallet.createorder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.createorder.R

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

        private const val KEY_PRIMARY_TICKER = "_PRIMARY_TICKER"
        private const val KEY_SECONDARY_TICKER = "_SECONDARY_TICKER"

        /**
         * Routes this activity to the [CreateOrderActivity].
         *
         * @param tradingPair The [TradingPair] that will be selected by default
         */
        fun route(activity: Activity, tradingPair: TradingPair? = null) {
            val intent = Intent(activity, CreateOrderActivity::class.java)
                    .putExtra(KEY_PRIMARY_TICKER, tradingPair?.primaryTicker)
                    .putExtra(KEY_SECONDARY_TICKER, tradingPair?.secondaryTicker)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_order

    override val isSecureActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}