package org.loopring.looprwallet.tradedetails.activities

import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.tradedetails.R

class TradingPairDetailsActivity : BaseActivity() {

    companion object {

        private const val KEY_TRADING_PAIR = "_TRADING_PAIR"

        fun route(tradingPair: Any, activity: BaseActivity) {
            val intent = Intent(activity, TradingPairDetailsActivity::class.java)
//                    .putExtra(KEY_TRADING_PAIR, )
            TODO("PUT DATA")

            activity.startActivity(intent)
        }

        private fun getPairFromIntent(intent: Intent): String {
            return intent.getStringExtra(KEY_TRADING_PAIR)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_trading_pair_details

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tradingPairPrimaryKey = getPairFromIntent(intent)
    }

}
