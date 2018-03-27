package com.caplaninnovations.looprwallet.fragments.security

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.click
import android.view.View
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import kotlinx.android.synthetic.main.number_pad.*
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Corey on 3/26/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EnterNewSecurityFragmentTest : BaseDaggerFragmentTest<EnterNewSecurityFragment>() {

    override val fragment = EnterNewSecurityFragment.createPinInstance()

    override val tag: String = EnterNewSecurityFragment.TAG

    @Test
    fun enterPin_thenEnterIncorrectPin() {
        assertEquals("", fragment.currentPin)
        assertNull(fragment.enteredPin)

        val numberPadOne = fragment.numberPadOne

        clickView(numberPadOne)
        assertEquals("1", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("11", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("111", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("", fragment.currentPin)

        // We should now be "confirming" the PIN we just entered
        assertTrue(fragment.isConfirmingEnteredPin)
        assertEquals("1111", fragment.enteredPin)

        val numberPadTwo = fragment.numberPadTwo

        clickView(numberPadTwo)
        assertEquals("2", fragment.currentPin)

        clickView(numberPadTwo)
        assertEquals("22", fragment.currentPin)

        clickView(numberPadTwo)
        assertEquals("222", fragment.currentPin)

        clickView(numberPadTwo)
        assertEquals("", fragment.currentPin)
        assertNull(fragment.enteredPin)
    }

    @Test
    fun enterPin_thenEnterCorrectPin() {
        assertEquals("", fragment.currentPin)
        assertNull(fragment.enteredPin)

        val numberPadOne = fragment.numberPadOne

        clickView(numberPadOne)
        assertEquals("1", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("11", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("111", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("", fragment.currentPin)
        assertEquals("1111", fragment.enteredPin)

        clickView(numberPadOne)
        assertEquals("1", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("11", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("111", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("1111", fragment.currentPin)
        assertEquals("1111", fragment.enteredPin)

        assertTrue(fragment.userPinSettings.isUserPinEqual("1111"))
    }

    @Test
    fun checkBackspaceWorks() {
        assertEquals("", fragment.currentPin)
        assertNull(fragment.enteredPin)

        val numberPadOne = fragment.numberPadOne
        val backspaceButton = fragment.numberPadBackspace

        clickView(numberPadOne)
        assertEquals("1", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("11", fragment.currentPin)

        clickView(backspaceButton)
        assertEquals("1", fragment.currentPin)
        clickView(backspaceButton)
        assertEquals("", fragment.currentPin)
        clickView(backspaceButton)
        assertEquals("", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("1", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("11", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("111", fragment.currentPin)

        clickView(numberPadOne)
        assertEquals("", fragment.currentPin)
        assertEquals("1111", fragment.enteredPin)

        clickView(backspaceButton)
        assertEquals("", fragment.currentPin)
        assertEquals("1111", fragment.enteredPin)
    }

    private fun clickView(view: View) = Espresso.onView(`is`(view)).perform(click())

}