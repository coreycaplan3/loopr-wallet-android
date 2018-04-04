package org.loopring.looprwallet.tokendetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.trading.TradingPair

class TradingPairDetailsActivity : BaseActivity() {

    companion object {

        private const val KEY_TRADING_PAIR = "_TRADING_PAIR"

        fun route(tradingPair: TradingPair, context: Context): Intent {
            return Intent(context, TradingPairDetailsActivity::class.java)
                    .putExtra(KEY_TRADING_PAIR, tradingPair.ticker)
        }

        private fun getPairFromIntent(intent: Intent): String {
            return intent.getStringExtra(KEY_TRADING_PAIR)
        }

    }

    override val contentView: Int
        get() = R.layout.activity_trading_pair_details

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tradingPairPrimaryKey = getPairFromIntent(intent)
    }

}
