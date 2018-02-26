package com.caplaninnovations.looprwallet.handlers

import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationTag
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.bottom_navigation_tab.view.*

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
                              private val fragmentStackHistory: FragmentStackHistory,
                              savedInstanceState: Bundle?) :
        TabLayout.OnTabSelectedListener {

    interface OnBottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val bottomNavigation = activity.bottomNavigation

    private var currentFragment: Fragment? = null

    init {
        setupTabs()

        when {
            savedInstanceState != null -> {
                val tag = fragmentStackHistory.peek()!!
                logv("Pushing $tag fragment...")

                // We don't need to select the tab actually, just update the UI
                updateTabUi(bottomNavigation.findTabByTag(tag), true)
                currentFragment = activity.supportFragmentManager.findFragmentByTag(tag)
            }
            else -> {
                val tag = initialTag
                logv("Initializing $tag fragment...")

                Handler().postDelayed({
                    // Needed to allow the TabLayout animation to occur initially.
                    onTabSelected(bottomNavigation.findTabByTag(tag))
                }, 200)
            }
        }

        bottomNavigation.addOnTabSelectedListener(this)
    }

    /**
     * Called when the activity's [BaseActivity.onBackPressed] method is called
     * @return True if the activity should be finished or false otherwise
     */
    fun onBackPressed(): Boolean {
        fragmentStackHistory.pop()

        fragmentStackHistory.peek()?.let {
            bottomNavigation.findTabByTag(it)?.select()
            return false
        }

        fragmentStackHistory.peek().let {
            // The stack is empty. Time to finish the activity
            return true
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        logv("Tag unselected: ${tab?.tag}")
        updateTabUi(tab, false)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        fun commitTransaction(fragment: Fragment, fragmentTag: String) {
            val controller = FragmentStackTransactionController(R.id.activityContainer, fragment, fragmentTag)
            controller.commitTransaction(activity.supportFragmentManager, currentFragment)
            currentFragment = fragment
        }

        logv("Tag selected: ${tab?.tag}")

        updateTabUi(tab, true)

        (tab?.tag as? String)?.let { newFragmentTag: String ->
            logv("Pushing $newFragmentTag onto stack...")

            fragmentStackHistory.push(newFragmentTag)

            val newFragment = activity.supportFragmentManager.findFragmentByTagOrCreate(newFragmentTag, fragmentTagPairs)

            val baseFragment = currentFragment as? BaseFragment
            if (baseFragment?.appbarLayout?.isExpanded() == false) {
                logd("Expanding appbar before committing transaction...")
                baseFragment.appbarLayout?.setExpanded(true, true)

                // Allow the appbar to snap into place for committing the transaction
                Handler().postDelayed({ commitTransaction(newFragment, newFragmentTag) },
                        125L)
            } else {
                commitTransaction(newFragment, newFragmentTag)
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        (tab?.tag as? String)?.let {
            val fragment = activity.supportFragmentManager.findFragmentByTag(it)
            (fragment as? OnBottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
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
        tab?.customView?.apply {
            val tabTextView = findViewById<TextView>(R.id.bottomNavigationTabText)
            if (isSelected) {
                tabTextView.isSelected = true
                animateTopPadding(R.dimen.bottom_navigation_margin_top_selected, R.integer.animation_duration_short)
                animateToWidth(R.dimen.bottom_navigation_width_selected, R.integer.animation_duration_short)
                animateToAlpha(1.toFloat(), R.integer.animation_duration_short)
                tabTextView.animateScaleBoth(1F, R.integer.animation_duration_short)
            } else {
                animateTopPadding(R.dimen.bottom_navigation_margin_top, R.integer.animation_duration_short)
                animateToWidth(R.dimen.bottom_navigation_width, R.integer.animation_duration_short)
                animateToAlpha(0.68.toFloat(), R.integer.animation_duration_short)
                tabTextView.animateScaleBoth(0F, R.integer.animation_duration_short)
            }
        }
    }

}