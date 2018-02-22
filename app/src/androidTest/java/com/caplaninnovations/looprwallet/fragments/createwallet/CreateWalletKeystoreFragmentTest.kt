package com.caplaninnovations.looprwallet.fragments.createwallet

import android.support.test.InstrumentationRegistry
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
import com.caplaninnovations.looprwallet.validation.BaseValidator
import junit.framework.Assert.*
import kotlinx.android.synthetic.main.card_create_wallet_name.*
import kotlinx.android.synthetic.main.card_create_wallet_password.*
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import org.hamcrest.Matchers.*
import org.junit.Assert.assertNotEquals
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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

    private val goodName = "corey"
    private val goodPassword = "coreycorey"

    private val badName = "corey corey" // there cannot be any spaces
    private val badPassword = "corey" // it's too short

    private val nullString: String? = null
    private val emptyString: String? = ""

    @Before
    fun setUp() {
        activityRule.activity.addFragment(fragment, CreateWalletKeystoreFragment.TAG)

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        Espresso.onView(`is`(fragment.createWalletNameEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.createWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordEmpty() {
        Espresso.onView(`is`(fragment.createWalletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.createWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameEmpty_passwordOkay() {
        Espresso.onView(`is`(fragment.createWalletNameEditText))
                .check(matches(withText(emptyString)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.createWalletPasswordEditText))
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
        Espresso.onView(`is`(fragment.createWalletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        assertNotNull(fragment.createWalletNameInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.createWalletNameInputLayout.error)

        Espresso.onView(`is`(fragment.createWalletPasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        assertNotNull(fragment.createWalletPasswordInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.createWalletPasswordInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay() {
        Espresso.onView(`is`(fragment.createWalletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.createWalletPasswordEditText))
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