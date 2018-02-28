package com.caplaninnovations.looprwallet.dialogs

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationKeystore
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.isDisabled
import kotlinx.android.synthetic.main.dialog_confirm_password.*
import org.hamcrest.Matchers.`is`
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
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
class ConfirmPasswordDialogTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var activity: TestActivity

    /**
     * The wallet name is okay
     */
    private val walletName = "loopr-wallet"


    private val password = "looprloopr"
    private val incorrectPassword = "abcdeabcde"

    private val dialog = ConfirmPasswordDialog.createInstance(
            WalletCreationKeystore(walletName, password)
    )

    private val nullString: String? = null
    private val emptyString = ""

    @Before
    fun setUp() {
        activity = activityRule.activity
        dialog.show(activity.supportFragmentManager, ConfirmPasswordDialog.TAG)
        waitForActivityToBeSetup()
    }

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        Espresso.onView(`is`(dialog.confirmPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(dialog.confirmButton))
                .check(isDisabled())
    }

    @Test
    fun passwordBad() {
        Espresso.onView(`is`(dialog.confirmPasswordEditText))
                .perform(typeText(incorrectPassword), closeSoftKeyboard())

        assertNotNull(dialog.confirmPasswordInputLayout.error)

        Espresso.onView(`is`(dialog.confirmButton))
                .check(isDisabled())
    }

    @Test
    fun passwordOkay_createKeystoreWallet() {
        Espresso.onView(`is`(dialog.confirmPasswordEditText))
                .perform(typeText(password), closeSoftKeyboard())
                .check(matches(withText(password)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(dialog.confirmButton))
                .check(matches(isEnabled()))
                .perform(click())

        assertActivityActive(MainActivity::class.java, 10000)
    }

    @Test
    fun passwordOkay_createPhraseWallet() {
        val phraseDialog = ConfirmPasswordDialog.createInstance(
                WalletCreationPhrase(walletName, password, listOf())
        )

        dialog.dismiss()
        phraseDialog.show(activity.supportFragmentManager, ConfirmPasswordDialog.TAG)
        waitForActivityToBeSetup()

        Espresso.onView(`is`(phraseDialog.confirmPasswordEditText))
                .perform(typeText(password), closeSoftKeyboard())
                .check(matches(withText(password)))
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(phraseDialog.confirmButton))
                .check(matches(isEnabled()))
                .perform(click())

        // TODO - We need to implement these kinds of wallets
        assertActivityActive(MainActivity::class.java)
    }

}