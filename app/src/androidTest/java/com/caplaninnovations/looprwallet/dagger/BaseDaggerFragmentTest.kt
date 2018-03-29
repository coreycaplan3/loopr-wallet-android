package com.caplaninnovations.looprwallet.dagger

import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule

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

}