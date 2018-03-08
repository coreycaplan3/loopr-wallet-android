package com.caplaninnovations.looprwallet.activities

import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.utilities.OrientationChangeAction
import com.caplaninnovations.looprwallet.utilities.logi
import com.caplaninnovations.looprwallet.utilities.str
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask
import android.app.Instrumentation.ActivityMonitor
import android.net.sip.SipErrorCode.TIME_OUT
import android.app.Activity
import android.os.Bundle
import org.junit.After


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
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    private val activity by lazy {
        activityRule.activity
    }

    private val progressMessage = "hello-loopr"

    private lateinit var activityMonitor: ActivityMonitor

    @Before
    fun setup() {
        activityRule.launchActivity(null)
        activityMonitor = instrumentation.addMonitor(MainActivity::class.java.name, null, false)

        waitForActivityToBeSetup()
    }

    @After
    fun tearDown() {
        instrumentation.removeMonitor(activityMonitor)
    }

    @Test
    fun startProgressDialog() {
        val showTask = FutureTask {
            activity.progressDialog.setMessage(progressMessage)
            activity.progressDialog.show()
        }

        waitForTask(activity, showTask, false)

        // Assert it's still showing, after the orientation change
        assertTrue(activity.progressDialog.isShowing)
        assertEquals(progressMessage, activity.progressDialogMessage)

        val dismissTask = FutureTask { activity.progressDialog.dismiss() }
        waitForTask(activity, dismissTask, false)

        assertFalse(activity.progressDialog.isShowing)
    }

}