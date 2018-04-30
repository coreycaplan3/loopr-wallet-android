package org.loopring.looprwallet.core.presenters

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.ViewGroup
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.isExpanded
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.logv
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentSwitcherPagerAdapter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment

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
 * @param container The container in which the pager will be used
 * @param pagerAdapter The adapter that works in sync with the activity container
 * @param stackHistory The history of all the fragment's back-stack
 */
class BottomNavigationPresenter(bottomNavigationView: BottomNavigationView,
                                container: ViewGroup,
                                pagerAdapter: LooprFragmentSwitcherPagerAdapter,
                                private val stackHistory: BottomNavigationFragmentStackHistory,
                                savedInstanceState: Bundle?) {

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    companion object {

        private const val KEY_PAGER_ADAPTER_STATE = "_PAGER_ADAPTER_STATE"
    }

    private val container by weakReference(container)
    private val bottomNavigationView by weakReference(bottomNavigationView)
    private var pagerAdapter by weakReference(pagerAdapter)

    init {

        when {
            savedInstanceState != null -> {
                val tag = stackHistory.peek()!!
                logv("Pushing $tag fragment...")

                pagerAdapter.restoreState(savedInstanceState.getParcelable(KEY_PAGER_ADAPTER_STATE), savedInstanceState.classLoader)
            }
            else -> {
                executeFragmentTransaction(stackHistory.peek()!!)
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
        if (currentFragment is SearchFragment && currentFragment.searchViewPresenter.isExpanded) {
            // The SearchView is expanded. Let's collapse it and return
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

        outState?.putParcelable(KEY_PAGER_ADAPTER_STATE, pagerAdapter?.saveState())
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
            val container = container ?: return
            if (position != null) {
                pagerAdapter?.instantiateFragment(container, position)
            }
        }

        stackHistory.push(newFragmentTitle)

        val appBarLayout = (pagerAdapter?.currentFragment as? BaseFragment)?.appbarLayout
        if (appBarLayout != null && !appBarLayout.isExpanded()) {
            logv("Expanding appbar before committing transaction...")

            appBarLayout.setExpanded(true, true)

            // Allow the appbar to snap into place for committing the transaction
            runBlocking {
                delay(200)
                setCurrentPage(newFragmentTitle)
            }
        } else {
            setCurrentPage(newFragmentTitle)
        }
    }

}