package com.caplaninnovations.looprwallet.models.android.navigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.MarketsParentFragment
import com.caplaninnovations.looprwallet.fragments.MyWalletFragment
import com.caplaninnovations.looprwallet.fragments.OrdersParentFragment
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 *  Created by Corey on 1/31/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 * Handles all activity related to bottom navigation. Note, this class should be cleared out in the
 * activity's
 *
 * This class should be instantiated in the corresponding activity's [BaseActivity.onCreate] method.
 */
class BottomNavigationHandler(private val activity: BaseActivity, savedInstanceState: Bundle?) :
        TabLayout.OnTabSelectedListener {

    interface OnTabVisibilityChangeListener {

        fun onShowTabLayout(tabLayout: TabLayout?)
        fun onHideTabLayout(tabLayout: TabLayout?)
    }

    interface OnBottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    companion object Tags {

        private const val KEY_MARKETS = "_MARKETS"
        private const val KEY_ORDERS = "_ORDERS"
        private const val KEY_MY_WALLET = "_MY_WALLET"
    }

    private val bottomNavigation = activity.bottomNavigation

    /**
     * A list of pairs that points a fragment tag to a function that will create its fragment
     */
    private val fragmentTagPairs: List<Pair<String, Fragment>> = listOf<Pair<String, Fragment>>(
            Pair(KEY_MARKETS, MarketsParentFragment()),
            Pair(KEY_ORDERS, OrdersParentFragment()),
            Pair(KEY_MY_WALLET, MyWalletFragment())
    )

    private val fragmentStackHistory = FragmentStackHistory(savedInstanceState)

    private var currentFragment: Fragment? = null

    private var animatorForHidingTab: Animator? = null

    init {
        setupTabs()

        if (savedInstanceState == null) {
            val tag = KEY_MARKETS
            logv("Initializing $tag fragment...")

            onTabSelected(bottomNavigation.findTabByTag(tag))
        } else {
            val tag = fragmentStackHistory.peek()!!
            logv("Pushing $tag fragment...")

            updateTabUi(bottomNavigation.findTabByTag(tag), true)
            currentFragment = activity.supportFragmentManager.findFragmentByTag(tag)
        }

        bottomNavigation.addOnTabSelectedListener(this)
    }

    /**
     * Called when the activity's [BaseActivity.onBackPressed] method is called
     * @return True if the activity should be finished or false otherwise
     */
    fun onBackPressed(): Boolean {
        fragmentStackHistory.pop()

        if (fragmentStackHistory.isEmpty()) {
            return true
        } else {
            bottomNavigation.findTabByTag(fragmentStackHistory.peek()!!)?.select()
            return false
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        logv("Tag unselected: ${tab?.tag}")
        updateTabUi(tab, false)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        logv("Tag selected: ${tab?.tag}")

        updateTabUi(tab, true)

        (tab?.tag as? String)?.let { tag: String ->
            logv("Pushing $tag onto stack...")
            fragmentStackHistory.push(tag)

            val newFragment = activity.supportFragmentManager.findFragmentByTagOrCreate(tag, fragmentTagPairs)
            FragmentStackTransactionController(R.id.activityContainer, newFragment, tag, currentFragment)
                    .commitTransactionNow(activity.supportFragmentManager)

            currentFragment = newFragment
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        (tab?.tag as? String)?.let {
            val fragment = activity.supportFragmentManager.findFragmentByTag(it)
            (fragment as? OnBottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
    }

    fun onHideTabLayout(tabLayout: TabLayout?) {
        animatorForHidingTab =
                tabLayout?.animateFromHeight(
                        activity.getResourceIdFromAttrId(android.R.attr.actionBarSize),
                        R.integer.tab_layout_animation_duration)
    }

    fun onSaveInstanceState(outState: Bundle?) {
        fragmentStackHistory.saveState(outState)
    }

    // MARK - Private Methods

    private fun setupTabs() {

        fun createTab(tag: String, @DrawableRes iconRes: Int, @StringRes textRes: Int): TabLayout.Tab {
            val tabView = activity.layoutInflater.inflate(R.layout.bottom_navigation_tab, bottomNavigation, false)
            tabView.findById<ImageView>(R.id.bottomNavigationTabImage)?.setImageResource(iconRes)
            tabView.findById<TextView>(R.id.bottomNavigationTabText)?.setText(textRes)

            val tab = bottomNavigation.newTab()
                    .setCustomView(tabView)
                    .setTag(tag)

            if (fragmentStackHistory.peek() == tag) {
                tab.select()
            }

            return tab
        }

        bottomNavigation.addTab(
                createTab(KEY_MARKETS, R.drawable.ic_show_chart_white_24dp, R.string.markets)
        )

        bottomNavigation.addTab(
                createTab(KEY_ORDERS, R.drawable.ic_assignment_white_24dp, R.string.orders)
        )

        bottomNavigation.addTab(
                createTab(KEY_MY_WALLET, R.drawable.ic_account_balance_wallet_white_24dp, R.string.my_wallet)
        )
    }

    private fun updateTabUi(tab: TabLayout.Tab?, isSelected: Boolean) {
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
        }, activity.resources.getInteger(R.integer.ripple_duration).toLong())
    }

    fun onShowTabLayout(tabLayout: TabLayout?) {
        fun runAnimation() {
            tabLayout?.animateToHeight(
                    activity.getResourceIdFromAttrId(android.R.attr.actionBarSize),
                    R.integer.tab_layout_animation_duration)
        }

        if (animatorForHidingTab?.isRunning == true) {
            animatorForHidingTab?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    runAnimation()
                }
            })
        } else {
            runAnimation()
        }
    }

}