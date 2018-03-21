package com.caplaninnovations.looprwallet.fragments.signin

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.dialogs.ConfirmPasswordDialog
import com.caplaninnovations.looprwallet.fragments.createwallet.CreateWalletRememberPhraseFragment
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.isDisabled
import com.caplaninnovations.looprwallet.validators.BaseValidator
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_sign_in_enter_password.*
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.FutureTask

/**
 * Created by Corey on 3/6/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: For testing the [EnterPasswordForPhraseFragment.createCreationInstance]
 * version of the fragment.
 */
class EnterPasswordForPhraseCreationFragmentTest : BaseDaggerFragmentTest<EnterPasswordForPhraseFragment>() {

    override val fragment = EnterPasswordForPhraseFragment.createCreationInstance()
    override val tag = EnterPasswordForPhraseFragment.TAG

    private val badName = "loopr$" // bad character
    private val goodName = "loopr"

    private val badPassword = "loopr" // too short
    private val goodPassword = "looprwallet"

    private val emptyString = ""
    private val nullString: String? = null

    @Test
    fun viewText_isCorrect() {
        fragment.apply {
            assertEquals(str(R.string.enter_a_strong_password), enterWalletPasswordTitleLabel.text)
            assertEquals(str(R.string.generate_phrase), rememberGeneratePhraseButton.text)
            assertEquals(str(R.string.safety_create_phrase), enterPhrasePasswordSafetyLabel.text)
        }
    }

    @Test
    fun initialState_buttonDisabled_textEmpty() {
        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.rememberGeneratePhraseButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        // Still empty, so error should be empty
        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.rememberGeneratePhraseButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameBad_passwordBad() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        assertNotNull(fragment.walletNameInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletNameInputLayout.error)

        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        assertNotNull(fragment.walletPasswordInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletPasswordInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.rememberGeneratePhraseButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.rememberGeneratePhraseButton))
                .check(matches(isEnabled()))
                .perform(click())

        val fragment = activity.supportFragmentManager.findFragmentByTag(ConfirmPasswordDialog.TAG)
        assertEquals(ConfirmPasswordDialog.TAG, fragment.tag)
    }

    @Test
    fun onPasswordConfirmed() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        val task = FutureTask { fragment.onPasswordConfirmed() }
        waitForTask(activity, task, false)

        Thread.sleep(500)

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(CreateWalletRememberPhraseFragment.TAG, fragment.tag)
    }

}