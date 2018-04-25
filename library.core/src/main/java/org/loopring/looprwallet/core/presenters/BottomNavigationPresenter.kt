package org.loopring.looprwallet.core.presenters

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentPagerAdapter

/**
 * Created by Corey on 1/31/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Handles all things related to bottom navigation for an activity with bottom
 * navigation enabled.
 *
 * This class should be instantiated in the corresponding activity's [Activity.onCreate] method.
 *
 * @param activity The activity in which this presenter resides
 * @param bottomNavigationView The [BottomNavigationView] in which the tabs are displayed
 * @param viewPager The locked [ViewPager] in which these items are located
 * @param pagerAdapter The adapter that works in sync with the [viewPager]
 * @param bottomNavigationFragmentStackHistory The history of all the fragment's back-stack
 */
class BottomNavigationPresenter(activity: BaseActivity,
                                bottomNavigationView: BottomNavigationView,
                                viewPager: ViewPager,
                                pagerAdapter: LooprFragmentPagerAdapter,
                                private val bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory,
                                savedInstanceState: Bundle?) {

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val activity by weakReference(activity)
    private val bottomNavigationView by weakReference(bottomNavigationView)
    private var currentFragment by weakReference<Fragment>(null)
    private var viewPager by weakReference(viewPager)
    private var pagerAdapter by weakReference(pagerAdapter)

    init {
        when {
            savedInstanceState != null -> {
                val tag = bottomNavigationFragmentStackHistory.peek()!!
                logv("Pushing $tag fragment...")

                // We don't need to select the tab actually, just update the UI
                currentFragment = activity.supportFragmentManager.findFragmentById(viewPager.id)
            }
            else -> {
                Handler().postDelayed({
                    // Needed to allow the TabLayout animation to occur initially.
                    viewPager.adapter = pagerAdapter
                }, 200)
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            executeFragmentTransaction(it.title.toString())
            return@setOnNavigationItemSelectedListener true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            val fragment = activity.supportFragmentManager.findFragmentById(viewPager.id)
            if (fragment is BottomNavigationReselectedLister) {
                fragment.onBottomNavigationReselected()
            } else {
                loge("Could not reselect ${fragment?.tag}!", IllegalStateException())
            }
        }
    }

    /**
     * Called when the activity's [BaseActivity.onBackPressed] method is called
     * @return True if the activity should be finished or false otherwise
     */
    fun onBackPressed(): Boolean {
        bottomNavigationFragmentStackHistory.pop()

        bottomNavigationFragmentStackHistory.peek()?.let {
            getMenuIdFromTitle(it)?.let {
                bottomNavigationView?.selectedItemId = it
                return false
            }
        }

        // The stack is empty. Time to finish the activity
        return true
    }

    fun onSaveInstanceState(outState: Bundle?) {
        bottomNavigationFragmentStackHistory.saveState(outState)
    }

    // MARK - Private Methods

    private fun getMenuIdFromTitle(title: String): Int? {
        val menu = bottomNavigationView?.menu ?: return null
        for (i in 0 until menu.size()) {
            if (menu.getItem(i).title == title) {
                return menu.getItem(i).itemId
            }
        }
        return null

    }

    private fun getPositionFromTitle(title: String): Int? {
        val pagerAdapter = pagerAdapter ?: return null
        for (i in 0 until pagerAdapter.count) {
            if (pagerAdapter.getPageTitle(i) == title) {
                return i
            }
        }
        return null
    }

    private fun executeFragmentTransaction(newFragmentTitle: String) {

        fun commitTransaction(title: String) {
            val position = getPositionFromTitle(title)
            if (position != null) {
                viewPager?.let {
                    it.setCurrentItem(position, false)
                    currentFragment = activity?.supportFragmentManager?.findFragmentById(it.id)
                }
            }
        }

        bottomNavigationFragmentStackHistory.push(newFragmentTitle)

        val appBarLayout = (currentFragment as? BaseFragment)?.appbarLayout
        if (appBarLayout != null && !appBarLayout.isExpanded()) {
            logv("Expanding appbar before committing transaction...")

            appBarLayout.setExpanded(true, true)

            // Allow the appbar to snap into place for committing the transaction
            runBlocking {
                delay(125L)
                commitTransaction(newFragmentTitle)
            }
        } else {
            commitTransaction(newFragmentTitle)
        }
    }

}