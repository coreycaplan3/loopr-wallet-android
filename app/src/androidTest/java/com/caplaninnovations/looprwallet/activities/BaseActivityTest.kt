package com.caplaninnovations.looprwallet.activities

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import com.caplaninnovations.looprwallet.utilities.str
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
        val showTask = FutureTask {
            activity.progressDialog.setMessage(str(R.string.app_name))
            activity.progressDialog.show()
        }

        waitForTask(activity, showTask, false)

        onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())

        // Assert it's still showing, after the orientation change
        assertTrue(activity.progressDialog.isShowing)
        assertEquals(str(R.string.app_name), activity.progressDialogMessage)

        val dismissTask = FutureTask { activity.progressDialog.dismiss() }
        waitForTask(activity, dismissTask, false)

        onView(isRoot()).perform(OrientationChangeAction.changeOrientationToPortrait())

        assertFalse(activity.progressDialog.isShowing)
    }

}