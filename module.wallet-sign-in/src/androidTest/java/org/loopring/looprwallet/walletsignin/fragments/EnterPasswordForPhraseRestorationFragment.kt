package org.loopring.looprwallet.walletsignin.fragments

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_sign_in_enter_password.*
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.CustomViewAssertions
import org.loopring.looprwallet.core.utilities.CustomViewAssertions.isDisabled
import org.loopring.looprwallet.core.validators.BaseValidator
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.walletsignin.fragments.signin.EnterPasswordForPhraseFragment
import org.loopring.looprwallet.walletsignin.fragments.signin.SignInEnterPhraseFragment

/**
 * Created by Corey on 3/7/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class EnterPasswordForPhraseRestorationFragment : BaseDaggerFragmentTest<EnterPasswordForPhraseFragment>() {

    override fun provideFragment() = EnterPasswordForPhraseFragment.createRestorationInstance()
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
            Assert.assertEquals(str(R.string.enter_old_password), enterWalletPasswordTitleLabel.text)
            Assert.assertEquals(str(R.string.enter_phrase), rememberGeneratePhraseButton.text)
            Assert.assertEquals(str(R.string.safety_recover_phrase), enterPhrasePasswordSafetyLabel.text)
        }
    }

    @Test
    fun initialState_buttonDisabled_textEmpty() {
        Espresso.onView(Matchers.`is`(fragment.walletPasswordEditText))
                .check(matches(ViewMatchers.withText(emptyString)))

        Espresso.onView(Matchers.`is`(fragment.walletNameEditText))
                .check(matches(ViewMatchers.withText(emptyString)))

        Espresso.onView(Matchers.`is`(fragment.rememberGeneratePhraseButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordEmpty() {
        Espresso.onView(Matchers.`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(ViewMatchers.hasErrorText(nullString)))

        // Still empty, so error should be empty
        Espresso.onView(Matchers.`is`(fragment.walletPasswordEditText))
                .check(matches(ViewMatchers.hasErrorText(nullString)))
                .check(matches(ViewMatchers.withText(emptyString)))

        Espresso.onView(Matchers.`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(Matchers.`is`(fragment.rememberGeneratePhraseButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameBad_passwordBad() {
        Espresso.onView(Matchers.`is`(fragment.walletNameEditText))
                .perform(typeText(badName), closeSoftKeyboard())

        Assert.assertNotNull(fragment.walletNameInputLayout.error)
        Assert.assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletNameInputLayout.error)

        Espresso.onView(Matchers.`is`(fragment.walletPasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        Assert.assertNotNull(fragment.walletPasswordInputLayout.error)
        Assert.assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletPasswordInputLayout.error)

        Espresso.closeSoftKeyboard()

        Espresso.onView(Matchers.`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(Matchers.`is`(fragment.rememberGeneratePhraseButton))
                .check(CustomViewAssertions.isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay() {
        Espresso.onView(Matchers.`is`(fragment.walletNameEditText))
                .perform(typeText(goodName), closeSoftKeyboard())
                .check(matches(ViewMatchers.hasErrorText(nullString)))

        Espresso.onView(Matchers.`is`(fragment.walletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(ViewMatchers.hasErrorText(nullString)))

        Espresso.onView(Matchers.`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(Matchers.`is`(fragment.rememberGeneratePhraseButton))
                .check(matches(ViewMatchers.isEnabled()))
                .perform(click())

        val confirmPasswordFragment = activity.supportFragmentManager.findFragmentByTag(ConfirmPasswordDialog.TAG)
        assertNull(confirmPasswordFragment)

        val enterPhraseFragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(SignInEnterPhraseFragment.TAG, enterPhraseFragment.tag)
    }

}