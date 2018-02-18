package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.markets.MarketsParentFragment
import com.caplaninnovations.looprwallet.fragments.mywallet.MyWalletFragment
import com.caplaninnovations.looprwallet.fragments.orders.OrdersParentFragment
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MARKETS
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MY_WALLET
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_ORDERS
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class: As of right now, this will translate into the *Market* screen for traders,
 * when they first open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity(), BottomNavigationHandler.OnTabVisibilityChangeListener {

    companion object {

        const val KEY_FINISH_ALL = "_FINISH_ALL"

        fun createIntentToFinishApp(): Intent {
            return Intent(LooprWalletApp.application.applicationContext, MainActivity::class.java)
                    .putExtra(KEY_FINISH_ALL, true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_main

    override val isSecurityActivity: Boolean
        get() = true

    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentTagPairs: List<BottomNavigationFragmentPair> = listOf<BottomNavigationFragmentPair>(
                BottomNavigationFragmentPair(KEY_MARKETS, MarketsParentFragment(),
                        R.drawable.ic_show_chart_white_24dp, R.string.markets),

                BottomNavigationFragmentPair(KEY_ORDERS, OrdersParentFragment(),
                        R.drawable.ic_assignment_white_24dp, R.string.orders),

                BottomNavigationFragmentPair(KEY_MY_WALLET, MyWalletFragment(),
                        R.drawable.ic_account_balance_wallet_white_24dp, R.string.my_wallet)

        )

        bottomNavigationHandler = BottomNavigationHandler(this, fragmentTagPairs,
                KEY_MARKETS, savedInstanceState)

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

    override fun onShowTabLayout(tabLayout: TabLayout?) {
        bottomNavigationHandler.onShowTabLayout(tabLayout)
    }

    override fun onHideTabLayout(tabLayout: TabLayout?) {
        bottomNavigationHandler.onHideTabLayout(tabLayout)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        bottomNavigationHandler.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        bottomNavigation.clearOnTabSelectedListeners()
    }

}
