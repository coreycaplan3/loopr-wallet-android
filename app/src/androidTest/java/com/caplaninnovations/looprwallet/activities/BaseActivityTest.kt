package com.caplaninnovations.looprwallet.activities

import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import kotlinx.android.synthetic.main.appbar_main.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Created by Corey Caplan on 2/5/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class BaseActivityTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    @Test
    fun onCreate() {
    }

    @Test
    fun startProgressDialogAfterRotation() {
        activityRule.activity.progressDialog.setTitle(R.string.app_name)
        activityRule.activity.progressDialog.show()

        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape())

        // Assert it's still showing, after the orientation change
        assertTrue(activityRule.activity.progressDialog.isShowing)

        activityRule.activity.progressDialog.dismiss()

        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait())

        assertFalse(activityRule.activity.progressDialog.isShowing)
    }

    @Test
    fun checkToolbarModeAfterRotation() {
        activityRule.activity.enableToolbarCollapsing(null)

        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape())
        assertTrue(activityRule.activity.isToolbarCollapseEnabled)

        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait())
        assertFalse(activityRule.activity.isToolbarCollapseEnabled)
    }

    @Test
    fun updateContainerBasedOnToolbarMode() {
        activityRule.activity.enableToolbarCollapsing(null)

        val container = getCurrentFragmentContainer()
        activityRule.activity.updateContainerBasedOnToolbarMode(container)

        val enabledBehavior = (container.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
        assertEquals(AppBarLayout.ScrollingViewBehavior::class.java, enabledBehavior::class.java)

        // Disable toolbar collapsing
        activityRule.activity.disableToolbarCollapsing(null)

        activityRule.activity.updateContainerBasedOnToolbarMode(container)

        val disabledBehavior = (container.layoutParams as CoordinatorLayout.LayoutParams).behavior
        assertNull(disabledBehavior)

    }

    @Test
    fun enableToolbarCollapsing() {
        activityRule.activity.enableToolbarCollapsing(null)

        val toolbarLayoutParams = activityRule.activity.toolbar.layoutParams as AppBarLayout.LayoutParams
        val flags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
        assertEquals(flags, toolbarLayoutParams.scrollFlags)
    }

    @Test
    fun disableToolbarCollapsing() {
        activityRule.activity.disableToolbarCollapsing(null)

        val toolbarLayoutParams = activityRule.activity.toolbar.layoutParams as AppBarLayout.LayoutParams
        assertEquals(0, toolbarLayoutParams.scrollFlags)
    }

    private fun getCurrentFragmentContainer(): ViewGroup {
        val fragmentManager = activityRule.activity.supportFragmentManager
        val fragment = (fragmentManager.findFragmentById(R.id.activityContainer) as BaseFragment)
        return fragment.container!!
    }


}