package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mock

/**
 * Created by Corey on 2/24/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class WalletNameValidatorTest {

    @Mock
    lateinit var textInputLayout: TextInputLayout

    private lateinit var validator: WalletNameValidator

    private val invalidBadCharacter = "loopr123"
    private val invalidBadSpacing = "loopr loopr"
    private val validName = "loopr-loopr_loopr"

    @Before
    fun setUp() {
        validator = WalletNameValidator(textInputLayout, {})
    }

    @Test
    fun invalid_empty() {

    }

    @Test
    fun invalid_null() {

    }

    @Test
    fun invalid_badSpacing() {

    }

    @Test
    fun invalid_badCharacter() {

    }

    @Test
    fun invalid_tooShort() {

    }

    @Test
    fun invalid_tooLong() {

    }

    @Test
    fun shouldBeValid() {
        assertTrue(validator.isValid(validName))
        assertNull(validator.error)
    }

}