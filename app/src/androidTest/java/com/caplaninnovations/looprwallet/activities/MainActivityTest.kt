package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.*
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
class MainActivityTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @get:Rule
    val activityIntentRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

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
    fun createIntentToFinishApplication() {
        val intent = MainActivity.createIntentToFinishApp()
        assertTrue(intent.getBooleanExtra(MainActivity.KEY_FINISH_ALL, false))
        assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK, intent.flags)
        assertEquals(MainActivity::class.java.`package`, intent.component.packageName)
        assertEquals(MainActivity::class.java.name, intent.component.className)
    }

    @Test
    fun onFinishApplication() {
        val intent = MainActivity.createIntentToFinishApp()
        activityIntentRule.launchActivity(intent)
        assertTrue(activityIntentRule.activity.isIntentForClosingApplication())
    }

    @Test
    fun onBackPressedOnce() {
        val task = FutureTask<Boolean> { bottomNavigationHandler.onBackPressed() }
        activityRule.activity.runOnUiThread(task)

        assertTrue(task.get())
    }

    @Test
    fun onBackPressedMultipleTimes() {
        // Current stack = MARKETS
        val bottomNavigationHandler = activityRule.activity.bottomNavigationHandler

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_ORDERS)))
                .perform(click())

        // Assert the tab selection propagated successfully
        // Current stack = ORDERS -- MARKETS
        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_MY_WALLET)))
                .perform(click())

        // Assert the tab selection propagated successfully
        // Current stack = MY_WALLET -- ORDERS -- MARKETS
        assertEquals(BottomNavigationHandler.KEY_MY_WALLET, bottomNavigationHandler.fragmentStackHistory.peek())

        onView(withTagValue(Matchers.`is`(BottomNavigationHandler.KEY_MARKETS)))
                .perform(click())

        // Assert the tab selection propagated successfully
        // Current stack = MARKETS -- MY_WALLET -- ORDERS
        assertEquals(BottomNavigationHandler.KEY_MARKETS, bottomNavigationHandler.fragmentStackHistory.peek())

        // Assert that the same fragment wasn't pushed on twice
        assertEquals(3, bottomNavigationHandler.fragmentStackHistory.getStackSize())

        Espresso.pressBack()
        assertEquals(BottomNavigationHandler.KEY_MY_WALLET, bottomNavigationHandler.fragmentStackHistory.peek())

        Espresso.pressBack()
        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        Espresso.pressBackUnconditionally()
        assertTrue(bottomNavigationHandler.fragmentStackHistory.isEmpty())
    }

    @Test
    fun onTabUnselected() {
        // The markets tab is the default selected one

        val markets = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.KEY_MARKETS)!!
        assertNotNull(markets)

        val task = FutureTask { bottomNavigationHandler.onTabUnselected(markets) }
        activityRule.activity.runOnUiThread(task)

        waitForAnimationsAndTask(task)

        val customView = markets.customView!!
        assertNotNull(customView)

        onView(Matchers.`is`(customView)).check(TopPaddingAssertion(R.dimen.bottom_navigation_margin_top))

        onView(Matchers.`is`(customView)).check(AlphaAssertion(0.68f))

        onView(Matchers.`is`(customView.findById<TextView>(R.id.bottomNavigationTabText)))
                .check(TextSizeAssertion(R.dimen.bottom_navigation_text_size))
    }

    @Test
    fun onTabSelected() {
        // The markets tab is the default selected one
        val orders = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.KEY_ORDERS)!!
        assertNotNull(orders)

        val task = FutureTask { bottomNavigationHandler.onTabSelected(orders) }
        activityRule.activity.runOnUiThread(task)

        waitForAnimationsAndTask(task)

        assertEquals(BottomNavigationHandler.KEY_ORDERS, bottomNavigationHandler.fragmentStackHistory.peek())

        val customView = orders.customView!!
        assertNotNull(customView)

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
        val tabs = activityRule.activity.marketsTabs
        assertNotNull(tabs)

        val task = FutureTask { bottomNavigationHandler.onHideTabLayout(tabs) }
        activityRule.activity.runOnUiThread(task)

        waitForAnimationsAndTask(task)

        onView(Matchers.`is`(tabs)).check(HeightAssertion(R.dimen.zero))
    }

    @Test
    fun onShowTabLayout() {
        val tabs = activityRule.activity.marketsTabs!!
        assertNotNull(tabs)

        val task = FutureTask { bottomNavigationHandler.onShowTabLayout(tabs) }
        activityRule.activity.runOnUiThread(task)

        waitForAnimationsAndTask(task)

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