package org.loopring.looprwallet.core.fragments.security

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import kotlinx.android.synthetic.main.fragment_security_pin.*
import kotlinx.android.synthetic.main.number_pad.*
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.models.settings.UserPinSettings
import javax.inject.Inject

/**
 * Created by Corey on 3/27/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class ConfirmOldSecurityFragmentTest : BaseDaggerFragmentTest<ConfirmOldSecurityFragment>() {

    override fun provideFragment(): ConfirmOldSecurityFragment {
        LooprSettings.getInstance(CoreLooprWalletApp.context)
                .putString(SecuritySettings.KEY_SECURITY_TYPE, SecuritySettings.TYPE_PIN_SECURITY)

        return ConfirmOldSecurityFragment.getUnlockAppInstance()
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
    fun enterIncorrectPin() {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(userPinSettings.isUserLockedOut())
    }

    @Test
    fun enterIncorrectPin_lockout() {
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

        assertTrue(userPinSettings.getLockoutTimeLeft() >= 1000L)

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
    fun enterCorrectPin() {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadTwo)
        clickView(fragment.numberPadThree)
        clickView(fragment.numberPadFour)

        assertTrue(activity.isSecurityConfirmed)
    }

}