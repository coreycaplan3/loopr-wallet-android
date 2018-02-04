package com.caplaninnovations.looprwallet.dagger

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule

/**
 * Created by Corey Caplan on 2/4/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseActivityDaggerTest<T : Activity> : BaseDaggerTest() {

    abstract val clazz: Class<T>

    @Rule
    lateinit var activityRule: ActivityTestRule<T>

    @Before
    fun setupBaseActivityDaggerTest() {
        activityRule = ActivityTestRule<T>(clazz)
    }

}