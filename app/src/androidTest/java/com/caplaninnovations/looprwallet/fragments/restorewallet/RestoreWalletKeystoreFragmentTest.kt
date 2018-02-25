package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions
import com.caplaninnovations.looprwallet.validators.BaseValidator
import kotlinx.android.synthetic.main.fragment_restore_private_key.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Created by Corey on 2/25/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class RestoreWalletKeystoreFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private val fragment = RestoreWalletKeystoreFragment()

    @Before
    fun setUp() {
        activityRule.activity.addFragment(fragment, RestoreWalletKeystoreFragment.TAG)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

}