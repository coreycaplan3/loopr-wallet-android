package org.loopring.looprwallet.core.fragments.security

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withText
import kotlinx.android.synthetic.main.fragment_security_pin.*
import kotlinx.android.synthetic.main.number_pad.*
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.models.settings.UserPinSettings
import org.loopring.looprwallet.core.utilities.BuildUtility.SECURITY_LOCKOUT_TIME
import javax.inject.Inject

/**
 * Created by Corey on 3/27/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class ConfirmOldSecurityFragmentTest : BaseDaggerFragmentTest<ConfirmOldSecurityFragment>() {

    override val fragment: ConfirmOldSecurityFragment
        get() {
            LooprSettings.getInstance(CoreLooprWalletApp.context)
                    .putString(SecuritySettings.KEY_SECURITY_TYPE, SecuritySettings.TYPE_PIN_SECURITY)

            return ConfirmOldSecurityFragment.createUnlockAppInstance()
        }

    override val tag = ConfirmOldSecurityFragment.TAG

    @Inject
    lateinit var userPinSettings: UserPinSettings

    @Before
    fun setUp() {
        testCoreLooprComponent.inject(this)

        userPinSettings.setUserPin("1234")
    }

    @Test
    fun enterIncorrectPin() = runBlockingUiCode {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(userPinSettings.isUserLockedOut())
    }

    @Test
    fun enterIncorrectPin_lockout() = runBlockingUiCode {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertTrue(userPinSettings.isUserLockedOut())

        assertTrue(userPinSettings.getLockoutTimeLeft() >= SECURITY_LOCKOUT_TIME)

        Espresso.onView(`is`(fragment.fragmentSecurityPinTitleLabel))
                .check(matches(withText(containsString("You are locked out for"))))
                .check(matches(withText(containsString("seconds"))))

        assertFalse(fragment.numberPadOne.isEnabled)
        assertFalse(fragment.numberPadTwo.isEnabled)
        assertFalse(fragment.numberPadThree.isEnabled)
        assertFalse(fragment.numberPadFour.isEnabled)
        assertFalse(fragment.numberPadFive.isEnabled)
        assertFalse(fragment.numberPadSix.isEnabled)
        assertFalse(fragment.numberPadSeven.isEnabled)
        assertFalse(fragment.numberPadEight.isEnabled)
        assertFalse(fragment.numberPadNine.isEnabled)

        assertFalse(fragment.numberPadBackspace.isEnabled)
    }

    @Test
    fun enterCorrectPin() = runBlockingUiCode {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadTwo)
        clickView(fragment.numberPadThree)
        clickView(fragment.numberPadFour)

        assertTrue(activity.isSecurityConfirmed)
    }

}