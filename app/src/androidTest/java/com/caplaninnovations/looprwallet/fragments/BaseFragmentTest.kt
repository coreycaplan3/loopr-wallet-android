package com.caplaninnovations.looprwallet.fragments

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import kotlinx.android.synthetic.main.appbar_main.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
@RunWith(AndroidJUnit4::class)
class BaseFragmentTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var activity: BaseActivity

    private val baseFragment = object : BaseFragment() {
        override val layoutResource: Int
            get() = R.layout.fragment_test_container
    }

    @Before
    fun setup() {
        activity = activityRule.activity
        activityRule.activity.addFragment(baseFragment, BaseFragment::class.java.simpleName)
    }

    @Test
    fun checkToolbarModeAfterRotation() {
        val enableToolbarCollapsingTask = FutureTask { baseFragment.enableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, enableToolbarCollapsingTask, false)

        onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())
        assertTrue(baseFragment.isToolbarCollapseEnabled)

        val disableToolbarCollapsingTask = FutureTask { baseFragment.disableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, disableToolbarCollapsingTask, false)

        onView(isRoot()).perform(OrientationChangeAction.changeOrientationToPortrait())
        assertFalse(baseFragment.isToolbarCollapseEnabled)
    }

    @Test
    fun enableToolbarCollapsing__checkUi() {
        val task = FutureTask { baseFragment.enableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, task, false)

        val toolbarLayoutParams = activity.ordersToolbar.layoutParams as AppBarLayout.LayoutParams
        val flags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        assertEquals(flags, toolbarLayoutParams.scrollFlags)

        val fragmentContainer = baseFragment.view?.findViewById<ViewGroup>(R.id.fragmentContainer)
        val topMargin = (fragmentContainer!!.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        assertEquals(0, topMargin)
    }

    @Test
    fun disableToolbarCollapsing_checkUi() {
        val task = FutureTask { baseFragment.disableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, task, false)

        val toolbarLayoutParams = activity.ordersToolbar.layoutParams as AppBarLayout.LayoutParams
        assertEquals(0, toolbarLayoutParams.scrollFlags)

        val fragmentContainer = baseFragment.view?.findViewById<ViewGroup>(R.id.fragmentContainer)
        val topMargin = (fragmentContainer!!.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        val actionBarSizeResource = activity.getResourceIdFromAttrId(android.R.attr.actionBarSize)
        val actionBarSize = activity.resources.getDimension(actionBarSizeResource).toInt()
        assertEquals(actionBarSize, topMargin)
    }

}