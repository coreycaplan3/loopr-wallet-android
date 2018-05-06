package org.loopring.looprwallet.core.presenters

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.view.ViewGroup
import androidx.view.isVisible
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentSwitcherPagerAdapter
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.SearchFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import kotlin.math.roundToInt

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
 * @param activity The activity in which this presenter resides
 * @param fragmentList The list of fragments that will be presented by this [BottomNavigationPresenter].
 * @param stackHistory The history of all the fragment's back-stack
 * @param savedInstanceState The previous state that
 */
class BottomNavigationPresenter(bottomNavigationView: BottomNavigationView,
                                activity: BaseActivity,
                                fragmentList: List<Pair<String, BaseFragment>>,
                                private val stackHistory: BottomNavigationFragmentStackHistory,
                                savedInstanceState: Bundle?) {

    interface BottomNavigation {

        val bottomNavigationPresenter: BottomNavigationPresenter
    }

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    companion object {

        private const val KEY_PAGER_ADAPTER_STATE = "_PAGER_ADAPTER_STATE"
    }

    private val activity by weakReference(activity)
    private val bottomNavigationView by weakReference(bottomNavigationView)
    private val pagerAdapter: LooprFragmentSwitcherPagerAdapter

    val currentFragment
        get() = pagerAdapter.currentFragment

    private val container: ViewGroup = activity.findViewById(R.id.activityContainer)

    init {

        val pagerState: Bundle? = savedInstanceState?.getParcelable(KEY_PAGER_ADAPTER_STATE) as? Bundle
        val classLoader = savedInstanceState?.classLoader
        pagerAdapter = LooprFragmentSwitcherPagerAdapter(container, pagerState, classLoader, activity.supportFragmentManager, fragmentList)

        if (savedInstanceState == null) {
            stackHistory.push(pagerAdapter.getPageTitle(0)!!.toString())
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
                loge("Could not reselect ${fragment.tag}!", IllegalStateException())
            }
        }
    }

    /**
     * Called when the activity's [BaseActivity.onBackPressed] method is called
     * @return True if the activity should be finished or false otherwise
     */
    fun onBackPressed(): Boolean {
        val currentFragment = pagerAdapter.currentFragment
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

    fun showBottomNavigation() {
        bottomNavigationView?.isVisible = true

        activity?.findViewById<ViewGroup>(R.id.activityContainer)?.apply {
            val oldParams = this.layoutParams as CoordinatorLayout.LayoutParams
            oldParams.bottomMargin = dimen(context.theme.getResourceIdFromAttrId(android.R.attr.actionBarSize)).roundToInt()
            this.layoutParams = layoutParams
            invalidate()
        }
    }

    fun hideBottomNavigation() {
        bottomNavigationView?.isVisible = false

        activity?.findViewById<ViewGroup>(R.id.activityContainer)?.apply {
            val oldParams = this.layoutParams as CoordinatorLayout.LayoutParams
            oldParams.bottomMargin = 0
            this.layoutParams = layoutParams
            invalidate()
        }
    }

    fun onSaveInstanceState(outState: Bundle?) {
        stackHistory.saveState(outState)

        outState?.putParcelable(KEY_PAGER_ADAPTER_STATE, pagerAdapter.saveState())
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
                pagerAdapter.instantiateFragment(container, position)
            }
        }

        stackHistory.push(newFragmentTitle)

        val appBarLayout = (pagerAdapter.currentFragment as? BaseFragment)?.appbarLayout
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