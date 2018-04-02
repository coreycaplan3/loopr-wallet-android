package org.loopring.looprwallet.core.fragments

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import org.loopring.looprwallet.core.utilities.OrientationChangeAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
 */
@RunWith(AndroidJUnit4::class)
class BaseFragmentTest : BaseDaggerFragmentTest<BaseFragmentTest.TestingBaseFragment>() {

    class TestingBaseFragment : BaseFragment() {
        override val layoutResource: Int
            get() = R.layout.fragment_test_container

    }

    override val fragment = TestingBaseFragment()
    override val tag = "BaseFragment"

    @Test
    fun checkToolbarModeAfterRotation() {
        val enableToolbarCollapsingTask = FutureTask { fragment.enableToolbarCollapsing() }
        waitForTask(activity, enableToolbarCollapsingTask, true)

        onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())

        val recreatedFragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer) as BaseFragment
        assertTrue(recreatedFragment.isToolbarCollapseEnabled)
    }

    @Test
    fun enableToolbarCollapsing__checkUi() {
        val task = FutureTask { fragment.enableToolbarCollapsing() }
        waitForTask(activity, task, false)

        val toolbarLayoutParams = fragment.toolbar!!.layoutParams as AppBarLayout.LayoutParams
        val flags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        assertEquals(flags, toolbarLayoutParams.scrollFlags)

        val fragmentContainer = fragment.view?.findViewById<ViewGroup>(R.id.fragmentContainer)
        val topMargin = (fragmentContainer!!.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        assertEquals(0, topMargin)
    }

    @Test
    fun disableToolbarCollapsing_checkUi() {
        val task = FutureTask { fragment.disableToolbarCollapsing() }
        waitForTask(activity, task, false)

        val toolbarLayoutParams = fragment.toolbar!!.layoutParams as AppBarLayout.LayoutParams
        assertEquals(0, toolbarLayoutParams.scrollFlags)

        val fragmentContainer = fragment.view?.findViewById<ViewGroup>(R.id.fragmentContainer)
        val topMargin = (fragmentContainer!!.layoutParams as CoordinatorLayout.LayoutParams).topMargin
        val actionBarSizeResource = activity.theme.getResourceIdFromAttrId(android.R.attr.actionBarSize)
        val actionBarSize = activity.resources.getDimension(actionBarSizeResource).toInt()
        assertEquals(actionBarSize, topMargin)
    }

}