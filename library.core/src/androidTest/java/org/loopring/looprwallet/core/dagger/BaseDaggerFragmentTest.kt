package org.loopring.looprwallet.core.dagger

import android.support.annotation.IdRes
import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.loopring.looprwallet.core.activities.TestActivity

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
    fun setUpBaseFragmentTestForFragment() = runBlockingUiCode {
        activity.addFragment(fragment, tag)
    }

    fun checkPreferenceKeyAndValue(key: String, expectedValue: String) {
        val value = LooprSettings.getInstance(fragment.context!!).getString(key)
        assertEquals(expectedValue, value)
    }

    fun checkPreferenceKeyAndValue(key: String, expectedValue: Int) {
        val value = LooprSettings.getInstance(fragment.context!!).getInt(key, -1)
        assertEquals(expectedValue, value)
    }

    fun checkCurrentFragmentByContainer(@IdRes container: Int, tag: String) {
        val fragment = activity.supportFragmentManager.findFragmentById(container)
        assertEquals(tag, fragment.tag)
    }

}