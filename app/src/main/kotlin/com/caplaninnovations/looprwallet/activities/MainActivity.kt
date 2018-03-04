package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.markets.MarketsParentFragment
import com.caplaninnovations.looprwallet.fragments.mywallet.MyWalletFragment
import com.caplaninnovations.looprwallet.fragments.orders.OrdersParentFragment
import com.caplaninnovations.looprwallet.fragments.transfers.ViewTransfersFragment
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MARKETS
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MY_WALLET
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_ORDERS
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_TRANSFERS
import kotlinx.android.synthetic.main.bottom_navigation.*

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

        const val KEY_FINISH_ALL = "_FINISH_ALL"

        /**
         * @return An intent used to kill the entire application, if started
         */
        fun createIntentToFinishApp(): Intent {
            return Intent(LooprWalletApp.application.applicationContext, MainActivity::class.java)
                    .putExtra(KEY_FINISH_ALL, true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * @return An intent used to start this activity (as normal), clearing any previous tasks
         * which may have pointed to here
         */
        fun createIntent(): Intent {
            return Intent(LooprWalletApp.getContext(), MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_main

    override val isSecurityActivity: Boolean
        get() = true

    override lateinit var fragmentStackHistory: FragmentStackHistory

    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentTagPairs: List<BottomNavigationFragmentPair> = listOf(
                BottomNavigationFragmentPair(KEY_MARKETS, MarketsParentFragment(),
                        R.drawable.ic_show_chart_white_24dp, R.string.markets),

                BottomNavigationFragmentPair(KEY_ORDERS, OrdersParentFragment(),
                        R.drawable.ic_assignment_white_24dp, R.string.orders),

                BottomNavigationFragmentPair(KEY_TRANSFERS, ViewTransfersFragment(),
                        R.drawable.ic_swap_horiz_white_24dp, R.string.transfers),

                BottomNavigationFragmentPair(KEY_MY_WALLET, MyWalletFragment(),
                        R.drawable.ic_account_balance_wallet_white_24dp, R.string.my_wallet)
        )

        fragmentStackHistory = FragmentStackHistory(false, savedInstanceState)

        bottomNavigationHandler = BottomNavigationHandler(this, fragmentTagPairs,
                KEY_MARKETS, fragmentStackHistory, savedInstanceState)

        isIntentForClosingApplication()
    }

    /**
     * @return True if the intent for [getIntent] was sent to close the application or false
     * otherwise.
     */
    fun isIntentForClosingApplication(): Boolean {
        return if (intent.getBooleanExtra(KEY_FINISH_ALL, false)) {
            // The user requested to exit the app
            finish()
            true
        } else {
            false
        }
    }

    override fun onBackPressed() {
        if (bottomNavigationHandler.onBackPressed()) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bottomNavigation.clearOnTabSelectedListeners()
    }

}
