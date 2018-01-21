package com.caplaninnovations.looprwallet.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.MarketsParentFragment
import com.caplaninnovations.looprwallet.fragments.MyWalletFragment
import com.caplaninnovations.looprwallet.fragments.OrdersParentFragment
import com.caplaninnovations.looprwallet.models.android.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.FragmentTransactionController
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
abstract class BottomNavigationActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    interface OnBottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val tagMarkets = "markets"
    private val tagOrders = "orders"
    private val tagMyWallet = "myWallet"

    private var isInitialized = false

    /**
     * A list of pairs that points a fragment tag to a function that will create its fragment
     */
    private val fragmentTagCreationPairs = listOf<Pair<String, () -> Fragment>>(
            Pair(tagMarkets, { MarketsParentFragment() }),
            Pair(tagOrders, { OrdersParentFragment() }),
            Pair(tagMyWallet, { MyWalletFragment() })
    )

    private lateinit var fragmentStackHistory: FragmentStackHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigation.addOnTabSelectedListener(this)

        fragmentStackHistory = ViewModelProviders.of(this).get(FragmentStackHistory::class.java)

        setupTabs()

        fragmentStackHistory.currentFragment.observe(this, Observer<String?> {
            it?.let {
                if (!isInitialized && savedInstanceState != null) {
                    logv("Selecting a new tab from saved instance...")

                    bottomNavigation.findTabByTag(it)?.select()
                    isInitialized = true
                } else {
                    logv("Creating fragment transaction...")

                    val fragment = supportFragmentManager.findFragmentOrCreate(it, fragmentTagCreationPairs)
                    FragmentTransactionController(R.id.mainActivityContainer, fragment, it)
                            .commitTransactionNow(supportFragmentManager)
                }

            }
        })

        if (savedInstanceState == null) {
            fragmentStackHistory.push(tagMarkets)
        } else {

        }

    }

    /*
     * Tab-Operations
     */

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        updateTab(tab, false)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        updateTab(tab, true)

        tab?.tag?.let {
            fragmentStackHistory.push(tab.tag as String)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        (tab?.tag as? String)?.let {
            val fragment = supportFragmentManager.findFragmentByTag(it)
            (fragment as? OnBottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
    }

    override fun onBackPressed() {
        fragmentStackHistory.pop()

        if (fragmentStackHistory.isEmpty()) {
            super.onBackPressed()
        } else {
            // Pop triggers a live-data change to our current fragment
            fragmentStackHistory.pop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bottomNavigation.clearOnTabSelectedListeners()
    }

    // MARK - Private Methods

    /**
     * I'm loving that elvis operator!
     *
     * @param tag The tag used to find the fragment
     * @param creationFunction A function used to create the fragment, if it's not found
     */
    private fun getFragmentByTagOrCreate(tag: String, creationFunction: () -> Fragment): Fragment {
        return supportFragmentManager.findFragmentByTag(tag) ?: creationFunction()
    }

    private fun setupTabs() {

        fun createTab(tag: String, @DrawableRes iconRes: Int, @StringRes textRes: Int): TabLayout.Tab {
            val tabView = layoutInflater.inflate(R.layout.bottom_navigation_tab, bottomNavigation, false)
            tabView.findById<ImageView>(R.id.bottomNavigationTabImage)?.setImageResource(iconRes)
            tabView.findById<TextView>(R.id.bottomNavigationTabText)?.setText(textRes)

            return bottomNavigation.newTab()
                    .setCustomView(tabView)
                    .setTag(tag)
        }

        bottomNavigation.addTab(
                createTab(tagMarkets, R.drawable.ic_show_chart_white_24dp, R.string.markets)
        )

        bottomNavigation.addTab(
                createTab(tagOrders, R.drawable.ic_assignment_white_24dp, R.string.orders)
        )

        bottomNavigation.addTab(
                createTab(tagMyWallet, R.drawable.ic_account_balance_wallet_white_24dp, R.string.my_wallet)
        )
    }

    private fun updateTab(tab: TabLayout.Tab?, isSelected: Boolean) {
        Handler().postDelayed({
            tab?.let {
                if (isSelected) {
                    tab.customView?.animateTopPadding(R.dimen.bottom_navigation_margin_top_selected)

                    tab.customView?.animateAlpha(1.toFloat())

                    tab.customView?.findById<TextView>(R.id.bottomNavigationTabText)
                            ?.animateTextSizeChange(R.dimen.bottom_navigation_text_size_selected)

                } else {
                    tab.customView?.animateTopPadding(R.dimen.bottom_navigation_margin_top)

                    tab.customView?.animateAlpha(0.68.toFloat())

                    tab.customView?.findById<TextView>(R.id.bottomNavigationTabText)
                            ?.animateTextSizeChange(R.dimen.bottom_navigation_text_size)
                }
            }
        }, resources.getInteger(R.integer.ripple_duration).toLong())
    }

}