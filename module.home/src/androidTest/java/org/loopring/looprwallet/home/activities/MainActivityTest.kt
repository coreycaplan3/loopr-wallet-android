package org.loopring.looprwallet.home.activities

import android.content.Intent
import android.os.Bundle
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.navigation.BottomNavigationFragmentPair
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
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

    private lateinit var bottomNavigationPresenter: BottomNavigationPresenter
    private lateinit var bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory

    @Before
    fun setUp() {
        activityRule.launchActivity(null)

        waitForActivityToBeSetup()

        bottomNavigationPresenter = activity.bottomNavigationPresenter
        bottomNavigationFragmentStackHistory = activity.bottomNavigationFragmentStackHistory

        Thread.sleep(250) // wait the bottom navigation handler to be initialized
    }

    @Test
    fun createIntentToFinishApplication() {
        val intent = SettingsActivity.createIntentToFinishApp()
        assertTrue(intent.getBooleanExtra(SettingsActivity.KEY_FINISH_ALL, false))
        assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK, intent.flags)
        assertEquals(activityRule.activity.packageName, intent.component.packageName)
        assertEquals(MainActivity::class.java.name, intent.component.className)
    }

    @Test
    fun onFinishApplication() {
        val launchIntent = SettingsActivity.createIntentToFinishApp()
        activityIntentRule.launchActivity(launchIntent)

        val activityIntent = activityIntentRule.activity.intent
        assertTrue(activityIntent.getBooleanExtra(SettingsActivity.KEY_FINISH_ALL, false))
        assertTrue(activityIntentRule.activity.isFinishing)
    }

    @Test
    fun onBackPressedOnce() {
        val task = FutureTask<Boolean> { bottomNavigationPresenter.onBackPressed() }
        activityRule.activity.runOnUiThread(task)

        assertTrue(task.get())
    }

    @Test
    fun onBackPressedMultipleTimes() {
        // Current stack = MARKETS
        assertEquals(BottomNavigationFragmentPair.KEY_MARKETS, bottomNavigationFragmentStackHistory.peek())

        onView(withId(`is`(R.id.menu_orders)))
                .perform(click())

        Thread.sleep(300)

        // Assert the tab selection propagated successfully
        // Current stack = ORDERS -- MARKETS
        assertEquals(BottomNavigationFragmentPair.KEY_ORDERS, bottomNavigationFragmentStackHistory.peek())

        onView(withId(`is`(R.id.menu_my_wallet)))
                .perform(click())

        Thread.sleep(300)

        // Assert the tab selection propagated successfully
        // Current stack = MY_WALLET -- ORDERS -- MARKETS
        assertEquals(BottomNavigationFragmentPair.KEY_MY_WALLET, bottomNavigationFragmentStackHistory.peek())

        /*
         * For some reason espresso bugs out and won't let us click here... so we'll do it the "old
         * fashion way"
         */
        onView(withId(`is`(R.id.menu_markets)))
                .perform(click())

        Thread.sleep(300)

        // Assert the tab selection propagated successfully
        // Current stack = MARKETS -- MY_WALLET -- ORDERS; markets should not have been added twice
        assertEquals(BottomNavigationFragmentPair.KEY_MARKETS, bottomNavigationFragmentStackHistory.peek())

        Espresso.pressBack()
        assertEquals(BottomNavigationFragmentPair.KEY_MY_WALLET, bottomNavigationFragmentStackHistory.peek())

        Espresso.pressBack()
        assertEquals(BottomNavigationFragmentPair.KEY_ORDERS, bottomNavigationFragmentStackHistory.peek())

        Espresso.pressBackUnconditionally()
        assertTrue(bottomNavigationFragmentStackHistory.isEmpty())
    }

    @Test
    fun onSaveInstanceState() {
        val bundle = Bundle()

        bottomNavigationFragmentStackHistory.saveState(bundle)

        assertTrue(bundle.containsKey(BottomNavigationFragmentStackHistory.KEY_STACK))
        assertFalse(bundle.isEmpty)
    }

}