package com.caplaninnovations.looprwallet.handlers

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
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationTag
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * Created by Corey on 1/31/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Handles all things related to bottom navigation for an activity with bottom
 * navigation enabled.
 *
 * This class should be instantiated in the corresponding activity's [BaseActivity.onCreate] method.
 */
class BottomNavigationHandler(private val activity: BaseActivity,
                              private val fragmentTagPairs: List<BottomNavigationFragmentPair>,
                              @BottomNavigationTag private val initialTag: String,
                              savedInstanceState: Bundle?) :
        TabLayout.OnTabSelectedListener {

    interface OnBottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val bottomNavigation = activity.bottomNavigation

    val fragmentStackHistory = FragmentStackHistory(savedInstanceState)

    private var currentFragment: Fragment? = null

    init {
        setupTabs()

        if (savedInstanceState == null) {
            val tag = initialTag
            logv("Initializing $tag fragment...")

            Handler().postDelayed({
                onTabSelected(bottomNavigation.findTabByTag(tag))
            }, 300L)
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

        return if (fragmentStackHistory.isEmpty()) {
            true
        } else {
            bottomNavigation.findTabByTag(fragmentStackHistory.peek()!!)?.select()
            false
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

            val controller = FragmentStackTransactionController(R.id.activityContainer, newFragment, tag)

            controller.commitTransactionNow(activity.supportFragmentManager, currentFragment)
            currentFragment = newFragment
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        (tab?.tag as? String)?.let {
            val fragment = activity.supportFragmentManager.findFragmentByTag(it)
            (fragment as? OnBottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
    }

    fun onSaveInstanceState(outState: Bundle?) {
        fragmentStackHistory.saveState(outState)
    }

    // MARK - Private Methods

    private fun setupTabs() {

        fun createTab(tag: String, @DrawableRes iconRes: Int, @StringRes textRes: Int): TabLayout.Tab {
            val tabView = activity.layoutInflater.inflate(R.layout.bottom_navigation_tab, bottomNavigation, false)
            tabView.findViewById<ImageView>(R.id.bottomNavigationTabImage)?.setImageResource(iconRes)
            tabView.findViewById<TextView>(R.id.bottomNavigationTabText)?.setText(textRes)
            tabView.tag = tag

            val tab = bottomNavigation.newTab()
                    .setCustomView(tabView)
                    .setTag(tag)

            if (fragmentStackHistory.peek() == tag) {
                tab.select()
            }

            return tab
        }

        fragmentTagPairs.forEach {
            bottomNavigation.addTab(
                    createTab(it.tag, it.drawableResource, it.textResource)
            )
        }

    }

    private fun updateTabUi(tab: TabLayout.Tab?, isSelected: Boolean) {
        Handler().postDelayed({
            tab?.let {
                if (isSelected) {
                    tab.customView?.animateTopPadding(R.dimen.bottom_navigation_margin_top_selected)

                    tab.customView?.animateAlpha(1.toFloat())

                    tab.customView?.findViewById<TextView>(R.id.bottomNavigationTabText)
                            ?.animateTextSizeChange(R.dimen.bottom_navigation_text_size_selected)

                } else {
                    tab.customView?.animateTopPadding(R.dimen.bottom_navigation_margin_top)

                    tab.customView?.animateAlpha(0.68.toFloat())

                    tab.customView?.findViewById<TextView>(R.id.bottomNavigationTabText)
                            ?.animateTextSizeChange(R.dimen.bottom_navigation_text_size)
                }
            }
        }, activity.resources.getInteger(R.integer.ripple_duration).toLong())
    }

}