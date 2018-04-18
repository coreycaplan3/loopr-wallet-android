package org.loopring.looprwallet.core.presenters

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationTag

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
 * @param fragmentTagPairs The pairs of fragments and their corresponding tags
 * @param initialTag The item that should be selected initially
 * @param bottomNavigationFragmentStackHistory The history of all the fragment's back-stack
 */
class BottomNavigationPresenter(activity: BaseActivity,
                                bottomNavigationView: BottomNavigationView,
                                private val fragmentTagPairs: List<BottomNavigationFragmentPair>,
                                @BottomNavigationTag private val initialTag: String,
                                private val bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory,
                                savedInstanceState: Bundle?) {

    interface BottomNavigationReselectedLister {

        fun onBottomNavigationReselected()
    }

    private val activity by weakReference(activity)

    private val bottomNavigationView by weakReference(bottomNavigationView)

    private var currentFragment by weakReference<Fragment>(null)

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

        bottomNavigationView.setOnNavigationItemSelectedListener {
            executeFragmentTransaction(getTagFromMenuId(it.itemId))
            true
        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            val tag = getTagFromMenuId(it.itemId)
            val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
            if (fragment is BottomNavigationReselectedLister) {
                fragment.onBottomNavigationReselected()
            } else {
                loge("Could not reselect fragment!", IllegalStateException())
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
            bottomNavigationView?.selectedItemId = getMenuIdFromTag(it)
            return false
        }

        // The stack is empty. Time to finish the activity
        return true
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
            val activity = activity ?: return
            activity.supportFragmentManager?.let {
                val controller = FragmentTransactionController(activity.activityContainerId, fragment, fragmentTag)
                controller.commitTransaction(it)
                currentFragment = fragment
            }
        }

        bottomNavigationFragmentStackHistory.push(newFragmentTag)

        val newFragment = activity?.supportFragmentManager?.findFragmentByTagOrCreate(newFragmentTag) { tag ->
            // The fragment MUST be in the immutable list (so it's ALWAYS safe to force the unwrap it).
            fragmentTagPairs.find { it.tag == tag }?.fragment!!
        } ?: return // We return since we CANNOT commit the transaction without this fragment

        val appBarLayout = (currentFragment as? BaseFragment)?.appbarLayout
        if (appBarLayout != null && !appBarLayout.isExpanded()) {
            logv("Expanding appbar before committing transaction...")

            appBarLayout.setExpanded(true, true)

            // Allow the appbar to snap into place for committing the transaction
            runBlocking {
                delay(125L)
                commitTransaction(newFragment, newFragmentTag)
            }
        } else {
            commitTransaction(newFragment, newFragmentTag)
        }
    }

}