package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.appbar_main.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import java.util.concurrent.FutureTask

/**
 * Created by Corey Caplan on 2/2/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class MainActivityTest: BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var bottomNavigationHandler: BottomNavigationHandler

    @Before
    fun setUp() {
        val task = FutureTask {
            bottomNavigationHandler = activityRule.activity.bottomNavigationHandler
        }

        activityRule.activity.runOnUiThread(task)
        task.get()
    }

    @Test
    fun onBackPressedOnce() {
        val task = FutureTask<Boolean> { bottomNavigationHandler.onBackPressed() }
        activityRule.activity.runOnUiThread(task)

        assertTrue(task.get())
    }

    @Test
    fun onBackPressedMultipleTimes() {
        val bottomNavigationHandler = activityRule.activity.bottomNavigationHandler

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_ORDERS)))
                .perform(click())

        // Assert the tab selection propagated successfully
        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_MY_WALLET)))
                .perform(click())

        // Assert the tab selection propagated successfully
        assertEquals(BottomNavigationHandler.KEY_MY_WALLET, bottomNavigationHandler.fragmentStackHistory.peek())

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_MARKETS)))
                .perform(click())

        // Assert the tab selection propagated successfully
        assertEquals(BottomNavigationHandler.KEY_MARKETS, bottomNavigationHandler.fragmentStackHistory.peek())

        // Assert that the same fragment wasn't pushed on twice
        assertEquals(3, bottomNavigationHandler.fragmentStackHistory.getStackSize())

        ViewActions.pressBack()
        assertEquals(BottomNavigationHandler.KEY_MY_WALLET, bottomNavigationHandler.fragmentStackHistory.peek())

        ViewActions.pressBack()
        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        // Can't use the view action's press back since it would throw an exception by leaving the activity
        val backPressedTask = FutureTask<Boolean> { bottomNavigationHandler.onBackPressed() }
        activityRule.activity.runOnUiThread(backPressedTask)
        assertTrue(backPressedTask.get())
    }

    @Test
    fun onTabUnselected() {
        // The markets tab is the default selected one

        val markets = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.KEY_MARKETS)!!
        val task = FutureTask { bottomNavigationHandler.onTabUnselected(markets) }
        activityRule.activity.runOnUiThread(task)

        // Wait for any possible animations to be complete
        task.get()

        val customView = markets.customView!!

        onView(Matchers.`is`(customView)).check(TopPaddingAssertion(R.dimen.bottom_navigation_margin_top))

        onView(Matchers.`is`(customView)).check(AlphaAssertion(0.68f))

        onView(Matchers.`is`(customView.findById<TextView>(R.id.bottomNavigationTabText)))
                .check(TextSizeAssertion(R.dimen.bottom_navigation_text_size))
    }

    @Test
    fun onTabSelected() {
        // The markets tab is the default selected one
        val task = FutureTask {
            val orders = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.KEY_ORDERS)!!
            bottomNavigationHandler.onTabSelected(orders)
        }
        activityRule.activity.runOnUiThread(task)

        // Wait for any possible animations to be complete
        task.get()
        Thread.sleep(300)

        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        val orders = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.KEY_ORDERS)!!
        val customView = orders.customView!!

        onView(Matchers.`is`(customView)).check(TopPaddingAssertion(R.dimen.bottom_navigation_margin_top_selected))

        onView(Matchers.`is`(customView)).check(AlphaAssertion(1.0F))

        onView(Matchers.`is`(customView.findById<TextView>(R.id.bottomNavigationTabText)))
                .check(TextSizeAssertion(R.dimen.bottom_navigation_text_size_selected))

    }

    @Test
    fun onTabReselected() {
    }

    @Test
    fun onHideTabLayout() {
        val tabs = activityRule.activity.marketsTabs!!
        val task = FutureTask { bottomNavigationHandler.onHideTabLayout(tabs) }
        activityRule.activity.runOnUiThread(task)

        // Wait for runnable to finish
        task.get()

        onView(Matchers.`is`(tabs)).check(HeightAssertion(R.dimen.zero))
    }

    @Test
    fun onShowTabLayout() {
        val tabs = activityRule.activity.marketsTabs!!
        val task = FutureTask { bottomNavigationHandler.onShowTabLayout(tabs) }
        activityRule.activity.runOnUiThread(task)

        task.get()

        val resource = activityRule.activity.getResourceIdFromAttrId(android.R.attr.actionBarSize)
        onView(Matchers.`is`(tabs)).check(HeightAssertion(resource))
    }

    @Test
    fun onSaveInstanceState() {
        val bundle = Bundle()
        bottomNavigationHandler.onSaveInstanceState(bundle)

        assertTrue(bundle.containsKey(FragmentStackHistory.KEY_STACK))

        assertFalse(bundle.isEmpty)
    }

}