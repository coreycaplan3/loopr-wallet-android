package com.caplaninnovations.looprwallet.models.android.navigation

import android.content.Context
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import com.caplaninnovations.looprwallet.activities.MainActivity
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.widget.TextView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.utilities.*
import kotlinx.android.synthetic.main.appbar_main.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import org.hamcrest.Matchers
import org.junit.Rule


/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class BottomNavigationHandlerTest {

    @Rule
    private val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var bottomNavigationHandler: BottomNavigationHandler

    @Before
    fun setUp() {
        bottomNavigationHandler = BottomNavigationHandler(activityRule.activity, null)
    }

    @Test
    fun onBackPressed() {
        assertTrue(bottomNavigationHandler.onBackPressed())

        bottomNavigationHandler = BottomNavigationHandler(activityRule.activity, null)

        val myWallet = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.Tags.KEY_MY_WALLET)
        bottomNavigationHandler.onTabSelected(myWallet)

        assertFalse(bottomNavigationHandler.onBackPressed())
        assertTrue(bottomNavigationHandler.onBackPressed())
    }

    @Test
    fun onTabUnselected() {
        // The markets tab is the default selected one
        val markets = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.Tags.KEY_MARKETS)!!
        activityRule.activity.runOnUiThread({ bottomNavigationHandler.onTabUnselected(markets) })

        // Wait for any possible animations to be complete
        Thread.sleep(300)

        val customView = markets.customView!!

        onView(Matchers.`is`(customView)).check(TopPaddingAssertion(R.dimen.bottom_navigation_margin_top))

        onView(Matchers.`is`(customView)).check(AlphaAssertion(0.68f))

        onView(Matchers.`is`(customView.findById<TextView>(R.id.bottomNavigationTabText)))
                .check(TextSizeAssertion(R.dimen.bottom_navigation_text_size))
    }

    @Test
    fun onTabSelected() {
        // The markets tab is the default selected one
        val orders = activityRule.activity.bottomNavigation.findTabByTag(BottomNavigationHandler.Tags.KEY_ORDERS)!!
        activityRule.activity.runOnUiThread({ bottomNavigationHandler.onTabUnselected(orders) })

        // Wait for any possible animations to be complete
        Thread.sleep(300)

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
        val tabs = activityRule.activity.marketsTabs
        activityRule.activity.runOnUiThread({ bottomNavigationHandler.onHideTabLayout(tabs) })

        Thread.sleep(300)

        onView(Matchers.`is`(tabs)).check(HeightAssertion(R.dimen.zero))
    }

    @Test
    fun onShowTabLayout() {
        val tabs = activityRule.activity.marketsTabs
        activityRule.activity.runOnUiThread({ bottomNavigationHandler.onShowTabLayout(tabs) })

        Thread.sleep(300)

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