package org.loopring.looprwallet.tradedetails.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.loopr.markets.TradingPair
import org.loopring.looprwallet.tradedetails.R
import org.loopring.looprwallet.tradedetails.fragments.TradingPairDetailsFragment

class TradingPairDetailsActivity : BaseActivity() {

    companion object {

        private const val KEY_MARKET = "MARKET"

        fun route(tradingPair: TradingPair, fragment: Fragment) {
            val intent = Intent(CoreLooprWalletApp.application, TradingPairDetailsActivity::class.java)
                    .putExtra(KEY_MARKET, tradingPair.market)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            fragment.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_trading_pair_details

    override val isSignInRequired: Boolean
        get() = true

    private val market: String
        get() = intent.getStringExtra(KEY_MARKET)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val tradingPair = TradingPair(market)
            val fragment = TradingPairDetailsFragment.getInstance(tradingPair)
            val tag = TradingPairDetailsFragment.TAG
            pushFragmentTransaction(fragment, tag, FragmentTransactionController.ANIMATION_VERTICAL)
        }
    }

}
