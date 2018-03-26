package com.caplaninnovations.looprwallet.handlers

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.android.fragments.BottomNavigationFragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationTag
import com.caplaninnovations.looprwallet.extensions.findFragmentByTagOrCreate
import com.caplaninnovations.looprwallet.extensions.isExpanded
import com.caplaninnovations.looprwallet.extensions.logv
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * Created by Corey on 1/31/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Handles all things related to bottom navigation for an activity with bottom
 * navigation enabled.
 *
 * This class should be instantiated in the corresponding activity's [Activity.onCreate] method.
 */
class BottomNavigationHandler(private val activity: BaseActivity,
                              private val fragmentTagPairs: List<BottomNavigationFragmentPair>,
                              @BottomNavigationTag private val initialTag: String,
                              private val bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory,
                              savedInstanceState: Bundle?) {

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val bottomNavigationView = activity.bottomNavigationView

    private var currentFragment: Fragment? = null

    init {

        when {
            savedInstanceState != null -> {
                val tag = bottomNavigationFragmentStackHistory.peek()!!
                logv("Pushing $tag fragment...")

                // We don't need to select the tab actually, just update the UI
                currentFragment = activity.supportFragmentManager.findFragmentByTag(tag)
            }
            else -> {
                val tag = initialTag
                logv("Initializing $tag fragment...")

                Handler().postDelayed({
                    // Needed to allow the TabLayout animation to occur initially.
                    executeFragmentTransaction(fragmentTagPairs.first().tag)
                }, 200)
            }
        }

        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            executeFragmentTransaction(getTagFromMenuId(it.itemId))
            true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            val tag = getTagFromMenuId(it.itemId)
            val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
            (fragment as? BottomNavigationReselectedLister)?.onBottomNavigationReselected()
        }
    }

    /**
     * Called when the activity's [BaseActivity.onBackPressed] method is called
     * @return True if the activity should be finished or false otherwise
     */
    fun onBackPressed(): Boolean {
        bottomNavigationFragmentStackHistory.pop()

        bottomNavigationFragmentStackHistory.peek()?.let {
            bottomNavigationView.selectedItemId = getMenuIdFromTag(it)
            return false
        }

        bottomNavigationFragmentStackHistory.peek().let {
            // The stack is empty. Time to finish the activity
            return true
        }
    }

    // MARK - Private Methods

    private fun getTagFromMenuId(id: Int): String {
        return fragmentTagPairs.find { it.menuId == id }!!.tag
    }

    private fun getMenuIdFromTag(tag: String): Int {
        return fragmentTagPairs.find { it.tag == tag }!!.menuId
    }

    private fun executeFragmentTransaction(newFragmentTag: String) {

        fun commitTransaction(fragment: Fragment, fragmentTag: String) {
            val controller = FragmentStackTransactionController(R.id.activityContainer, fragment, fragmentTag)
            controller.commitTransaction(activity.supportFragmentManager, currentFragment)
            currentFragment = fragment
        }

        bottomNavigationFragmentStackHistory.push(newFragmentTag)

        val newFragment = activity.supportFragmentManager.findFragmentByTagOrCreate(newFragmentTag) { tag ->
            fragmentTagPairs.find { it.tag == tag }?.fragment!!
        }

        val baseFragment = currentFragment as? BaseFragment
        if (baseFragment?.appbarLayout?.isExpanded() == false) {
            logv("Expanding appbar before committing transaction...")

            baseFragment.appbarLayout?.setExpanded(true, true)

            // Allow the appbar to snap into place for committing the transaction
            Handler().postDelayed({ commitTransaction(newFragment, newFragmentTag) }, 125L)
        } else {
            commitTransaction(newFragment, newFragmentTag)
        }
    }

}