package com.caplaninnovations.looprwallet.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.wallet.TradingPair

class TradingPairActivity : BaseActivity() {

    object IntentCreator {

        private const val tagTradingPair = "_TradingPair"

        fun createIntent(tradingPair: TradingPair, context: Context): Intent {
            return Intent(context, TradingPairActivity::class.java)
                    .putExtra(tagTradingPair, tradingPair.ticker)
        }

        internal fun getFromIntent(intent: Intent): String {
            return intent.getStringExtra(tagTradingPair)
        }

    }

    override val contentView: Int
        get() = R.layout.activity_trading_pair

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tradingPairPrimaryKey = IntentCreator.getFromIntent(intent)
    }

}
