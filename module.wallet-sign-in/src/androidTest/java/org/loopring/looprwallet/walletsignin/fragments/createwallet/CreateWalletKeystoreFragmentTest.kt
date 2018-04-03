package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.Manifest
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.MainActivity
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.core.utilities.CustomViewAssertions.isDisabled
import org.loopring.looprwallet.core.validators.BaseValidator
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.walletsignin.fragments.createwallet.CreateWalletKeystoreFragment
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
class CreateWalletKeystoreFragmentTest : BaseDaggerFragmentTest<CreateWalletKeystoreFragment>() {

    @Rule
    @JvmField
    val grantFilePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override val fragment = CreateWalletKeystoreFragment()
    override val tag = CreateWalletKeystoreFragment.TAG

    private val goodName = "loopr"
    private val goodPassword = "looprloopr"

    private val badName = "loopr loopr" // there cannot be any spaces
    private val badPassword = "loopr" // it's too short

    private val nullString: String? = null
    private val emptyString: String? = ""

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        Espresso.onView(`is`(fragment.walletNameEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(emptyString)))

        Espresso.onView(`is`(fragment.walletPasswordEditText))
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

        Espresso.onView(`is`(fragment.walletPasswordEditText))
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

        Espresso.onView(`is`(fragment.walletPasswordEditText))
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

        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .perform(typeText(badPassword), closeSoftKeyboard())

        assertNotNull(fragment.walletPasswordInputLayout.error)
        assertNotEquals(BaseValidator.DEFAULT_ERROR, fragment.walletPasswordInputLayout.error)

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

        Espresso.onView(`is`(fragment.walletPasswordEditText))
                .perform(typeText(goodPassword), closeSoftKeyboard())
                .check(matches(hasErrorText(nullString)))

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(swipeUp())

        Espresso.onView(`is`(fragment.createButton))
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

        assertActivityActive(MainActivity::class.java, 7500)
    }

}