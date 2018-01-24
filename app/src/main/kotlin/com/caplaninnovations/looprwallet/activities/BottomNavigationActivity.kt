package com.caplaninnovations.looprwallet.activities

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

    private val tagMarkets = "_Markets"
    private val tagOrders = "_Orders"
    private val tagMyWallet = "_MyWallet"

    /**
     * A list of pairs that points a fragment tag to a function that will create its fragment
     */
    private val fragmentTagPairs = listOf<Pair<String, Fragment>>(
            Pair(tagMarkets, MarketsParentFragment()),
            Pair(tagOrders, OrdersParentFragment()),
            Pair(tagMyWallet, MyWalletFragment())
    )

    private lateinit var fragmentStackHistory: FragmentStackHistory

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentStackHistory = FragmentStackHistory(savedInstanceState)

        setupTabs()

        if (savedInstanceState == null) {
            val tag = tagMarkets
            logv("Initializing $tag fragment...")

            onTabSelected(bottomNavigation.findTabByTag(tag))
        } else {
            val tag = fragmentStackHistory.peek()!!
            logv("Pushing $tag fragment...")

            updateTab(bottomNavigation.findTabByTag(tag), true)
            currentFragment = supportFragmentManager.findFragmentByTag(tag)
        }

        bottomNavigation.addOnTabSelectedListener(this)
    }

    override fun onBackPressed() {
        fragmentStackHistory.pop()

        if (fragmentStackHistory.isEmpty()) {
            finish()
        } else {
            bottomNavigation.findTabByTag(fragmentStackHistory.peek()!!)?.select()
        }
    }

    /*
     * Tab-Operations
     */

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        updateTab(tab, false)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        logv("Tag selected: ${tab?.tag}")
        updateTab(tab, true)

        (tab?.tag as? String)?.let { tag: String ->
            logv("Pushing $tag onto stack...")
            fragmentStackHistory.push(tag)

            val newFragment = supportFragmentManager.findFragmentByTagOrCreate(tag, fragmentTagPairs)
            FragmentTransactionController(R.id.mainActivityContainer, newFragment, tag, currentFragment)
                    .commitTransactionNow(supportFragmentManager)

            currentFragment = newFragment
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        (tab?.tag as? String)?.let {
            val fragment = supportFragmentManager.findFragmentByTag(it)
            (fragment as? OnBottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        fragmentStackHistory.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        bottomNavigation.clearOnTabSelectedListeners()
    }

    // MARK - Private Methods

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