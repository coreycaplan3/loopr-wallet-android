package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.utilities.CustomViewAssertions
import org.loopring.looprwallet.core.validators.BaseValidator
import kotlinx.android.synthetic.main.fragment_restore_private_key.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 2/25/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class RestoreWalletPrivateKeyFragmentTest : BaseDaggerFragmentTest<RestoreWalletPrivateKeyFragment>() {

    override val fragment = RestoreWalletPrivateKeyFragment()
    override val tag = RestoreWalletPrivateKeyFragment.TAG

    private val goodName = "loopr-currentWallet-private-key"
    private val badName = "loopr-currentWallet$"

    private val goodPrivateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e699"
    private val badPrivateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8ezzz"

    private val emptyString = ""
    private val nullString: String? = null

    @Test
    fun initialState_buttonDisabled_textEmpty() {
        Espresso.onView(`is`(fragment.privateKeyUnlockButton))
                .check(CustomViewAssertions.isDisabled())

        Espresso.onView(`is`(fragment.privateKeyEditText))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(withText(emptyString)))
    }

    @Test
    fun nameOkay_privateKeyEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        // Still empty, so error should be empty
        Espresso.onView(`is`(fragment.privateKeyEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.privateKeyUnlockButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameBad_privateKeyBad() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        assertNotNull(fragment.walletNameInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletNameInputLayout.error)

        Espresso.onView(`is`(fragment.privateKeyEditText))
                .perform(typeText(badPrivateKey), closeSoftKeyboard())

        assertNotNull(fragment.privateKeyInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.privateKeyInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.privateKeyUnlockButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameOkay_privateKeyOkay() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(withText(goodName)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.privateKeyEditText))
                .perform(typeText(goodPrivateKey), closeSoftKeyboard())
                .check(matches(withText(goodPrivateKey)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.privateKeyUnlockButton))
                .check(matches(isEnabled()))
                .perform(click())

        assertActivityActive(MainActivity::class.java)
    }

}