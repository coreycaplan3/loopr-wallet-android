package com.caplaninnovations.looprwallet.dialogs

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationKeystore
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.isDisabled
import kotlinx.android.synthetic.main.dialog_confirm_password.*
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 2/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(AndroidJUnit4::class)
class ConfirmPasswordDialogTest : BaseDaggerFragmentTest<ConfirmPasswordDialog>() {

    /**
     * The wallet name is okay
     */
    private val walletName = "loopr-currentWallet"

    private val password = "looprloopr"
    private val incorrectPassword = "abcdeabcde"

    override val fragment: ConfirmPasswordDialog = ConfirmPasswordDialog.createInstance(
            "null", WalletCreationKeystore(walletName, password)
    )

    override val tag = ConfirmPasswordDialog.TAG

    private val nullString: String? = null
    private val emptyString = ""

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        Espresso.onView(`is`(fragment.confirmPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.confirmButton))
                .check(isDisabled())
    }

    @Test
    fun passwordBad() {
        Espresso.onView(`is`(fragment.confirmPasswordEditText))
                .perform(typeText(incorrectPassword), closeSoftKeyboard())

        assertNotNull(fragment.confirmPasswordInputLayout.error)

        Espresso.onView(`is`(fragment.confirmButton))
                .check(isDisabled())
    }

    @Test
    fun passwordOkay() {
        Espresso.onView(`is`(fragment.confirmPasswordEditText))
                .perform(typeText(password), closeSoftKeyboard())
                .check(matches(withText(password)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.confirmButton))
                .check(matches(isEnabled()))
                .perform(click())

        assertFalse(fragment.isVisible)
    }

}