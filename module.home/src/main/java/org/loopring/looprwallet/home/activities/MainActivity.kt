package org.loopring.looprwallet.home.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment.OnSecurityConfirmedListener
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MARKETS
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_MY_WALLET
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_ORDERS
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair.Companion.KEY_TRANSFERS
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.home.R
import org.loopring.looprwallet.homeorders.fragments.HomeOrdersParentFragment
import org.loopring.looprwallet.homemarkets.fragments.HomeMarketsParentFragment
import org.loopring.looprwallet.homemywallet.fragments.MyWalletFragment
import org.loopring.looprwallet.hometransfers.fragments.ViewTransfersFragment
import org.loopring.looprwallet.walletsignin.activities.SignInActivity

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class: As of right now, this will translate into the *Market* screen for traders,
 * when they first open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity(), OnSecurityConfirmedListener {

    companion object {

        /**
         * @return An intent used to start this activity (as normal), clearing any previous tasks
         * which may have pointed to here
         */
        fun routeAndClearOldTasks(activity: Activity) {
            val intent = Intent(CoreLooprWalletApp.context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            activity.startActivity(intent)
        }
    }

    override val contentViewRes: Int
        get() = R.layout.activity_main

    override val isSecureActivity: Boolean
        get() = true

    private val fragmentTagPairs: List<BottomNavigationFragmentPair> = listOf(
            BottomNavigationFragmentPair(KEY_MARKETS, HomeMarketsParentFragment(), R.id.menu_markets),

            BottomNavigationFragmentPair(KEY_ORDERS, HomeOrdersParentFragment(), R.id.menu_orders),

            BottomNavigationFragmentPair(KEY_TRANSFERS, ViewTransfersFragment(), R.id.menu_transfers),

            BottomNavigationFragmentPair(KEY_MY_WALLET, MyWalletFragment(), R.id.menu_my_wallet)
    )

    lateinit var bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory

    lateinit var bottomNavigationPresenter: BottomNavigationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigationFragmentStackHistory = BottomNavigationFragmentStackHistory(false, savedInstanceState)

        bottomNavigationView.inflateMenu(R.menu.home_bottom_navigation_menu)

        bottomNavigationPresenter = BottomNavigationPresenter(
                activity = this,
                bottomNavigationView = bottomNavigationView,
                fragmentTagPairs = fragmentTagPairs,
                initialTag = KEY_MARKETS,
                bottomNavigationFragmentStackHistory = bottomNavigationFragmentStackHistory,
                savedInstanceState = savedInstanceState
        )
    }

    override fun onSecurityConfirmed(parameter: Int) {
        fragmentTagPairs.forEach {
            val fragment = it.fragment
            if (fragment is OnSecurityConfirmedListener) {
                fragment.onSecurityConfirmed(parameter)
            }
        }
    }

    override fun onBackPressed() {
        if (bottomNavigationPresenter.onBackPressed()) {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        bottomNavigationFragmentStackHistory.saveState(outState)
    }

    // MARK - Private Methods

    fun setupNavigationView() {

        val currentWalletName = walletClient.getCurrentWallet()?.walletName
        val allWallets = walletClient.getAllWallets()
        allWallets.forEachIndexed { index, item ->
            homeNavigationView.menu.add(index).let {
                it.title = item.walletName
                if (item.walletName == currentWalletName) {
                    it.isChecked = true
                }
            }
        }

        walletClient.getCurrentWallet()?.let {
            TODO("Bind values...")
        }

        homeNavigationView.setNavigationItemSelectedListener { menuItem ->
            when {
                menuItem.itemId == R.id.menuAddNewWallet -> {
                    SignInActivity.route(this)
                }
                else -> allWallets.find { it.walletName == menuItem.title }?.ifNotNull {
                    val currentWallet = walletClient.getCurrentWallet()
                    if (currentWallet != null && currentWallet != it) {
                        homeNavigationDrawerLayout.closeDrawers()

                        runBlocking {
                            delay(300L)

                            // The current wallet is not the selected one, so we must switch it
                            MainActivity.routeAndClearOldTasks(this@MainActivity)
                        }
                    }
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

}
