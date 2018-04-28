package org.loopring.looprwallet.core.presenters

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.isExpanded
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.logv
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentStatePagerAdapter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener

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
 * @param bottomNavigationView The [BottomNavigationView] in which the tabs are displayed
 * @param viewPager The locked [ViewPager] in which these items are located
 * @param pagerAdapter The adapter that works in sync with the [viewPager]
 * @param stackHistory The history of all the fragment's back-stack
 */
class BottomNavigationPresenter(bottomNavigationView: BottomNavigationView,
                                viewPager: ViewPager,
                                pagerAdapter: LooprFragmentStatePagerAdapter,
                                private val stackHistory: BottomNavigationFragmentStackHistory,
                                savedInstanceState: Bundle?) {

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val bottomNavigationView by weakReference(bottomNavigationView)
    private var viewPager by weakReference(viewPager)
    private var pagerAdapter by weakReference(pagerAdapter)

    init {
        viewPager.offscreenPageLimit = 0

        when {
            savedInstanceState != null -> {
                // We don't have anything to really do...
                val tag = stackHistory.peek()!!
                logv("Pushing $tag fragment...")
            }
            else -> {
                runBlocking {
                    delay(200)

                    // Needed to allow the TabLayout animation to occur initially.
                    viewPager.adapter = pagerAdapter
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            executeFragmentTransaction(it.title.toString())
            return@setOnNavigationItemSelectedListener true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            val fragment = pagerAdapter.currentFragment
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
        val currentFragment = pagerAdapter?.currentFragment
        if (currentFragment is OnSearchViewChangeListener && currentFragment.searchViewPresenter.isExpanded) {
            // The searchView is expanded. Let's collapse it and return
            currentFragment.searchViewPresenter.collapseSearchView()
            return false
        }

        stackHistory.pop()

        stackHistory.peek()?.let {
            getMenuIdFromTitle(it)?.let {
                bottomNavigationView?.selectedItemId = it
                return false
            }
        }

        // The stack is empty. Time to finish the activity
        return true
    }

    fun onSaveInstanceState(outState: Bundle?) {
        stackHistory.saveState(outState)
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

        fun setCurrentPage(title: String) {
            val position = getPositionFromTitle(title)
            if (position != null) {
                viewPager?.setCurrentItem(position, false)
            }
        }

        stackHistory.push(newFragmentTitle)

        val appBarLayout = (pagerAdapter?.currentFragment as? BaseFragment)?.appbarLayout
        if (appBarLayout != null && !appBarLayout.isExpanded()) {
            logv("Expanding appbar before committing transaction...")

            appBarLayout.setExpanded(true, true)

            // Allow the appbar to snap into place for committing the transaction
            runBlocking {
                delay(125L)
                setCurrentPage(newFragmentTitle)
            }
        } else {
            setCurrentPage(newFragmentTitle)
        }
    }

}