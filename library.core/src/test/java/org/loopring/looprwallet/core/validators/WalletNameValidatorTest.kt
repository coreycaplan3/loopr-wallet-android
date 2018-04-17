package org.loopring.looprwallet.core.validators

import android.support.design.widget.TextInputLayout
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.R
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
class WalletNameValidatorTest {

    @Mock
    lateinit var textInputLayout: TextInputLayout

    private lateinit var validator: WalletNameValidator

    private val invalidBadCharacter = "loopr123$"
    private val invalidBadSpacing = "loopr loopr"
    private val invalidShort = "a"
    private val invalidLong = "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde" +
            "abcdef"

    private val validName = "loopr-loopr_loopr-123"

    @Before
    fun setUp() {
        validator = WalletNameValidator(textInputLayout, {})
        validator = Mockito.spy(validator)

        Mockito.doReturn("required").`when`(validator).getTextFromResource(R.string.error_wallet_name_required)
        Mockito.doReturn("format").`when`(validator).getTextFromResource(R.string.error_wallet_name_bad_format)
        Mockito.doReturn("short").`when`(validator).getTextFromResource(R.string.error_wallet_name_too_short)
        Mockito.doReturn("long").`when`(validator).getTextFromResource(R.string.error_wallet_name_too_long)
    }

    @Test
    fun invalid_empty() {
        assertFalse(validator.isValid(""))
        assertEquals("required", validator.error)
    }

    @Test
    fun invalid_null() {
        assertFalse(validator.isValid(null))
        assertEquals("required", validator.error)
    }

    @Test
    fun invalid_badSpacing() {
        assertFalse(validator.isValid(invalidBadSpacing))
        assertEquals("format", validator.error)
    }

    @Test
    fun invalid_badCharacter() {
        assertFalse(validator.isValid(invalidBadCharacter))
        assertEquals("format", validator.error)
    }

    @Test
    fun invalid_tooShort() {
        assertFalse(validator.isValid(invalidShort))
        assertEquals("short", validator.error)
    }

    @Test
    fun invalid_tooLong() {
        assertFalse(validator.isValid(invalidLong))
        assertEquals("long", validator.error)
    }

    @Test
    fun shouldBeValid() {
        assertTrue(validator.isValid(validName))
        assertNull(validator.error)
    }

}