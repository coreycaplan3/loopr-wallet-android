package org.loopring.looprwallet.tradedetails.activities

import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.tradedetails.R
import org.loopring.looprwallet.tradedetails.fragments.TradingPairDetailsFragment

class TradingPairDetailsActivity : BaseActivity() {

    companion object {

        private const val KEY_PRIMARY_TICKER = "_PRIMARY_TICKER"
        private const val KEY_SECONDARY_TICKER = "_SECONDARY_TICKER"

        fun route(tradingPair: TradingPair, activity: BaseActivity) {
            val intent = Intent(activity, TradingPairDetailsActivity::class.java)
                    .putExtra(KEY_PRIMARY_TICKER, tradingPair.primaryTicker)
                    .putExtra(KEY_SECONDARY_TICKER, tradingPair.secondaryTicker)

            activity.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_trading_pair_details

    override val isSecureActivity: Boolean
        get() = true

    private val primaryTicker: String
        get() = intent.getStringExtra(KEY_PRIMARY_TICKER)

    private val secondaryTicker: String
        get() = intent.getStringExtra(KEY_SECONDARY_TICKER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val tradingPair = TradingPair(primaryTicker, secondaryTicker)
            pushFragmentTransaction(
                    TradingPairDetailsFragment.getInstance(tradingPair),
                    TradingPairDetailsFragment.TAG
            )
        }
    }

}
