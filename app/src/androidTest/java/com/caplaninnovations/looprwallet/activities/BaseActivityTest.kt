package com.caplaninnovations.looprwallet.activities

import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import kotlinx.android.synthetic.main.appbar_main.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask

/**
 * Created by Corey Caplan on 2/5/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(AndroidJUnit4::class)
class BaseActivityTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    private lateinit var activity: BaseActivity

    @Before
    fun setup() {
        activity = activityRule.activity
    }

    @Test
    fun startProgressDialogAfterRotation() {
        val task = FutureTask {
            activity.progressDialog.setTitle(R.string.app_name)
            activity.progressDialog.show()
        }

        waitForAnimationsAndTask(activity, task, false)

        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape())

        // Assert it's still showing, after the orientation change
        assertTrue(activity.progressDialog.isShowing)
        assertEquals(R.string.app_name, activity.progressDialogTitle)

        activity.progressDialog.dismiss()

        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait())

        assertFalse(activity.progressDialog.isShowing)
    }

    @Test
    fun checkToolbarModeAfterRotation() {
        val enableToolbarCollapsingTask = FutureTask { activity.enableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, enableToolbarCollapsingTask, false)

        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape())
        assertTrue(activity.isToolbarCollapseEnabled)

        val disableToolbarCollapsingTask = FutureTask { activity.disableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, disableToolbarCollapsingTask, false)

        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait())
        assertFalse(activity.isToolbarCollapseEnabled)
    }

    @Test
    fun enableToolbarCollapsing() {
        val task = FutureTask { activity.enableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, task, false)

        val toolbarLayoutParams = activity.toolbar.layoutParams as AppBarLayout.LayoutParams
        val flags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
        assertEquals(flags, toolbarLayoutParams.scrollFlags)

        val topMargin = (activity.activityContainer.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        assertEquals(0, topMargin)
    }

    @Test
    fun disableToolbarCollapsing() {
        val task = FutureTask { activity.disableToolbarCollapsing() }
        waitForAnimationsAndTask(activity, task, false)

        val toolbarLayoutParams = activity.toolbar.layoutParams as AppBarLayout.LayoutParams
        assertEquals(0, toolbarLayoutParams.scrollFlags)

        val topMargin = (activity.activityContainer.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        val actionBarSizeResource = activity.getResourceIdFromAttrId(android.R.attr.actionBarSize)
        val actionBarSize = activity.resources.getDimension(actionBarSizeResource).toInt()
        assertEquals(actionBarSize, topMargin)
    }

    private fun getCurrentFragmentContainer(): ViewGroup {
        val fragmentManager = activity.supportFragmentManager
        val fragment = (fragmentManager.findFragmentById(R.id.activityContainer) as BaseFragment)
        return fragment.container!!
    }

}