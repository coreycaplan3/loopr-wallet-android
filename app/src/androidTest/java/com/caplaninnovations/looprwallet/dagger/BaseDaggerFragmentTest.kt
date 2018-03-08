package com.caplaninnovations.looprwallet.dagger

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.activities.TestActivity
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 3/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseDaggerFragmentTest<out T : Fragment> : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)
    val activity: TestActivity by lazy {
        activityRule.activity
    }

    abstract val fragment: T
    abstract val tag: String

    @Before
    fun setUpBaseFragmentTestForFragment() {
        val task = FutureTask {
            if (fragment is DialogFragment) {
                (fragment as DialogFragment).show(activity.supportFragmentManager, tag)
            } else {
                activity.addFragment(fragment, tag)
            }
        }
        waitForTask(activity, task, false)

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

}