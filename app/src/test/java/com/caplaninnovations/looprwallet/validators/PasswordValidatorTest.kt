package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey on 2/24/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(MockitoJUnitRunner::class)
class PasswordValidatorTest {

    @Mock
    lateinit var textInputLayout: TextInputLayout

    lateinit var passwordValidator: PasswordValidator

    private val invalidTooShort = "abcdeabc"

    private val invalidTooLong = "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde"

    private val validPassword = "abcdeabcde"

    @Before
    fun setup() {
        passwordValidator = PasswordValidator(textInputLayout) {}
        passwordValidator = Mockito.spy(passwordValidator)
        Mockito.doReturn("required").`when`(passwordValidator).getTextFromResource(R.string.error_password_required)
        Mockito.doReturn("short").`when`(passwordValidator).getTextFromResource(R.string.error_password_too_short)
        Mockito.doReturn("long").`when`(passwordValidator).getTextFromResource(R.string.error_too_long)
    }

    @Test
    fun invalid_Empty() {
        assertFalse(passwordValidator.isValid(null))
        assertEquals("required", passwordValidator.error)
    }

    @Test
    fun invalid_tooShort() {
        assertFalse(passwordValidator.isValid(invalidTooShort))
        assertEquals("short", passwordValidator.error)
    }

    @Test
    fun invalid_tooLong() {
        assertFalse(passwordValidator.isValid(invalidTooLong))
        assertEquals("long", passwordValidator.error)
    }

    @Test
    fun shouldBeValid() {
        assertTrue(passwordValidator.isValid(validPassword))
        assertNull(passwordValidator.error)
    }

}