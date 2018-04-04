package org.loopring.looprwallet.home.activities

import android.content.Intent
import android.os.Bundle
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.LooprWalletCoreApp
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MARKETS
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MY_WALLET
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_ORDERS
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_TRANSFERS
import org.loopring.looprwallet.home.R
import org.loopring.looprwallet.home.fragments.markets.MarketsParentFragment
import org.loopring.looprwallet.home.fragments.orders.OrdersParentFragment
import org.loopring.looprwallet.home.fragments.wallet.MyWalletFragment
import org.loopring.looprwallet.home.handlers.BottomNavigationHandler
import org.loopring.looprwallet.transfer.fragments.ViewTransfersFragment

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class: As of right now, this will translate into the *Market* screen for traders,
 * when they first open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity() {

    companion object {

        /**
         * @return An intent used to start this activity (as normal), clearing any previous tasks
         * which may have pointed to here
         */
        fun createIntent(): Intent {
            return Intent(LooprWalletCoreApp.context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_main

    override val isSecureActivity: Boolean
        get() = true

    lateinit var bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory

    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentTagPairs: List<BottomNavigationFragmentPair> = listOf(
                BottomNavigationFragmentPair(KEY_MARKETS, MarketsParentFragment(), R.id.menu_markets),

                BottomNavigationFragmentPair(KEY_ORDERS, OrdersParentFragment(), R.id.menu_orders),

                BottomNavigationFragmentPair(KEY_TRANSFERS, ViewTransfersFragment(), R.id.menu_transfers),

                BottomNavigationFragmentPair(KEY_MY_WALLET, MyWalletFragment(), R.id.menu_my_wallet)
        )

        bottomNavigationFragmentStackHistory = BottomNavigationFragmentStackHistory(false, savedInstanceState)

        bottomNavigationHandler = BottomNavigationHandler(this, fragmentTagPairs,
                KEY_MARKETS, bottomNavigationFragmentStackHistory, savedInstanceState)
    }

    override fun onBackPressed() {
        if (bottomNavigationHandler.onBackPressed()) {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        bottomNavigationFragmentStackHistory.saveState(outState)
    }

}
