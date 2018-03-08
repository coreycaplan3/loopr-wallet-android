package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationFragmentPair
import com.caplaninnovations.looprwallet.handlers.BottomNavigationHandler
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.alphaIs
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.scaleXIs
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.scaleYIs
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.textSizeIs
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.topPaddingIs
import com.caplaninnovations.looprwallet.utilities.findTabByTag
import kotlinx.android.synthetic.main.bottom_navigation.*
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask

/**
 * Created by Corey Caplan on 2/2/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    @get:Rule
    val activityIntentRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    private val activity by lazy {
        activityRule.activity
    }

    private lateinit var bottomNavigationHandler: BottomNavigationHandler
    private lateinit var fragmentStackHistory: FragmentStackHistory

    @Before
    fun setUp() {
        activityRule.launchActivity(null)

        waitForActivityToBeSetup()

        bottomNavigationHandler = activity.bottomNavigationHandler
        fragmentStackHistory = activity.fragmentStackHistory

        Thread.sleep(250) // wait the bottom navigation handler to be initialized
    }

    @Test
    fun createIntentToFinishApplication() {
        val intent = MainActivity.createIntentToFinishApp()
        assertTrue(intent.getBooleanExtra(MainActivity.KEY_FINISH_ALL, false))
        assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK, intent.flags)
        assertEquals(activityRule.activity.packageName, intent.component.packageName)
        assertEquals(MainActivity::class.java.name, intent.component.className)
    }

    @Test
    fun onFinishApplication() {
        val launchIntent = MainActivity.createIntentToFinishApp()
        activityIntentRule.launchActivity(launchIntent)

        val activityIntent = activityIntentRule.activity.intent
        assertTrue(activityIntent.getBooleanExtra(MainActivity.KEY_FINISH_ALL, false))
        assertTrue(activityIntentRule.activity.isFinishing)
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
        assertEquals(BottomNavigationFragmentPair.KEY_MARKETS, fragmentStackHistory.peek())

        onView(withTagValue(`is`(BottomNavigationFragmentPair.KEY_ORDERS)))
                .perform(click())

        Thread.sleep(300)

        // Assert the tab selection propagated successfully
        // Current stack = ORDERS -- MARKETS
        assertEquals(BottomNavigationFragmentPair.KEY_ORDERS, fragmentStackHistory.peek())

        onView(withTagValue(`is`(BottomNavigationFragmentPair.KEY_MY_WALLET)))
                .perform(click())

        Thread.sleep(300)

        // Assert the tab selection propagated successfully
        // Current stack = MY_WALLET -- ORDERS -- MARKETS
        assertEquals(BottomNavigationFragmentPair.KEY_MY_WALLET, fragmentStackHistory.peek())

        /*
         * For some reason espresso bugs out and won't let us click here... so we'll do it the "old
         * fashion way"
         */
        val tab = activity.bottomNavigation.findTabByTag(BottomNavigationFragmentPair.KEY_MARKETS)
        val task = FutureTask { tab!!.select() }
        waitForTask(activity, task, true)

        // Assert the tab selection propagated successfully
        // Current stack = MARKETS -- MY_WALLET -- ORDERS; markets should not have been added twice
        assertEquals(BottomNavigationFragmentPair.KEY_MARKETS, fragmentStackHistory.peek())

        Espresso.pressBack()
        assertEquals(BottomNavigationFragmentPair.KEY_MY_WALLET, fragmentStackHistory.peek())

        Espresso.pressBack()
        assertEquals(BottomNavigationFragmentPair.KEY_ORDERS, fragmentStackHistory.peek())

        Espresso.pressBackUnconditionally()
        assertTrue(fragmentStackHistory.isEmpty())
    }

    @Test
    fun onTabUnselected_tabAnimationValues() {
        // The markets tab is the default selected one

        val markets = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationFragmentPair.KEY_MARKETS)!!

        val task = FutureTask { bottomNavigationHandler.onTabUnselected(markets) }
        waitForTask(activityRule.activity, task, true)

        // Wait extra time for everything to propagate
        Thread.sleep(300)

        val customView = markets.customView!!

        onView(`is`(customView))
                .check(topPaddingIs(R.dimen.bottom_navigation_margin_top))

        onView(`is`(customView))
                .check(alphaIs(0.68f))

        onView(`is`(customView.findViewById<TextView>(R.id.bottomNavigationTabText)))
                .check(textSizeIs(R.dimen.bottom_navigation_text_size_selected))
                .check(scaleXIs(0F))
                .check(scaleYIs(0F))
    }

    @Test
    fun onTabSelected_tabAnimationValues() {
        // The markets tab is the default selected one
        val orders = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationFragmentPair.KEY_ORDERS)!!

        val task = FutureTask { orders.select() }
        waitForTask(activityRule.activity, task, true)

        // Wait extra time for everything to propagate
        Thread.sleep(300)

        assertEquals(BottomNavigationFragmentPair.KEY_ORDERS, fragmentStackHistory.peek())

        val customView = orders.customView!!

        onView(`is`(customView))
                .check(topPaddingIs(R.dimen.bottom_navigation_margin_top_selected))

        onView(`is`(customView))
                .check(alphaIs(1.0F))

        onView(`is`(customView.findViewById<TextView>(R.id.bottomNavigationTabText)))
                .check(textSizeIs(R.dimen.bottom_navigation_text_size_selected))
                .check(scaleXIs(1.0F))
                .check(scaleYIs(1.0F))

    }

    @Test
    fun onSaveInstanceState() {
        val bundle = Bundle()

        fragmentStackHistory.saveState(bundle)

        assertTrue(bundle.containsKey(FragmentStackHistory.KEY_STACK))
        assertFalse(bundle.isEmpty)
    }

}