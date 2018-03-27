package com.caplaninnovations.looprwallet.fragments.security

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.caplaninnovations.looprwallet.BuildConfig.SECURITY_LOCKOUT_TIME
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import kotlinx.android.synthetic.main.fragment_security_pin.*
import kotlinx.android.synthetic.main.number_pad.*
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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

    override val fragment = ConfirmOldSecurityFragment.createUnlockAppInstance()
    override val tag = ConfirmOldSecurityFragment.TAG

    @Before
    fun setUp() {
        fragment.userPinSettings.setUserPin("1234")
    }

    @Test
    fun enterIncorrectPin() {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(fragment.userPinSettings.isUserLockedOut())
    }

    @Test
    fun enterIncorrectPin_lockout() {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(fragment.userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(fragment.userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(fragment.userPinSettings.isUserLockedOut())

        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertTrue(fragment.userPinSettings.isUserLockedOut())

        assertTrue(fragment.userPinSettings.getLockoutTimeLeft() >= SECURITY_LOCKOUT_TIME)

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