package com.caplaninnovations.looprwallet.fragments.createwallet

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.isDisabled
import com.caplaninnovations.looprwallet.validators.BaseValidator
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 2/20/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CreateWalletKeystoreFragmentTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private val fragment = CreateWalletKeystoreFragment()

    private val goodName = "loopr"
    private val goodPassword = "looprloopr"

    private val badName = "loopr loopr" // there cannot be any spaces
    private val badPassword = "loopr" // it's too short

    private val nullString: String? = null
    private val emptyString: String? = ""

    @Before
    fun setUp() {
        val task = FutureTask {
            activityRule.activity.addFragment(fragment, CreateWalletKeystoreFragment.TAG)
        }
        activityRule.activity.runOnUiThread(task)
        waitForTask(activityRule.activity, task, false)
        waitForActivityToBeSetup()
    }

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.enterWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.enterWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameEmpty_passwordOkay() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(withText(emptyString)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.enterWalletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(withText(goodPassword)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameBad_passwordBad() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        assertNotNull(fragment.walletNameInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletNameInputLayout.error)

        Espresso.onView(`is`(fragment.enterWalletPasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        assertNotNull(fragment.enterWalletPasswordInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.enterWalletPasswordInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.enterWalletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(matches(isEnabled()))
                .perform(click())

        val walletCreationData = WalletCreationKeystore(goodName, goodPassword)
        assertEquals(walletCreationData, fragment.walletCreationPasswordData.value)
    }

}