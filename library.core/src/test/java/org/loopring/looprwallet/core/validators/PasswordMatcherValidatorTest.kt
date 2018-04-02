package org.loopring.looprwallet.core.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey on 2/25/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(MockitoJUnitRunner::class)
class PasswordMatcherValidatorTest {

    @Mock
    lateinit var textInputLayout: TextInputLayout

    lateinit var passwordMatcherValidator: PasswordMatcherValidator

    private val invalidTooShort = "abcde"
    private val invalidIncorrect = "abcdeabcdeabcde"

    private val validPassword = "abcdeabcde"

    @Before
    fun setup() {
        passwordMatcherValidator = PasswordMatcherValidator(textInputLayout, validPassword) {}
        passwordMatcherValidator = Mockito.spy(passwordMatcherValidator)
        Mockito.doReturn("incorrect").`when`(passwordMatcherValidator).getTextFromResource(R.string.error_password_incorrect)
        Mockito.doReturn("short").`when`(passwordMatcherValidator).getTextFromResource(R.string.error_password_too_short)
    }

    @Test
    fun invalid_Empty() {
        assertFalse(passwordMatcherValidator.isValid(null))
        assertNull(passwordMatcherValidator.error)
    }

    @Test
    fun invalid_tooShort() {
        assertFalse(passwordMatcherValidator.isValid(invalidTooShort))
        assertEquals("short", passwordMatcherValidator.error)
    }

    @Test
    fun invalid_incorrect() {
        assertFalse(passwordMatcherValidator.isValid(invalidIncorrect))
        assertEquals("incorrect", passwordMatcherValidator.error)
    }

    @Test
    fun shouldBeValid() {
        assertTrue(passwordMatcherValidator.isValid(validPassword))
        assertNull(passwordMatcherValidator.error)
    }

}