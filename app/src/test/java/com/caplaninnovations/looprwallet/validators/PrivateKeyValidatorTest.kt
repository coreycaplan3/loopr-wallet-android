package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey on 2/24/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(MockitoJUnitRunner::class)
class PrivateKeyValidatorTest {

    @Mock
    lateinit var textInputLayout: TextInputLayout

    private lateinit var validator: PrivateKeyValidator

    private val badFormatPrivateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8ezzz"
    private val badLengthPrivateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e49311111"
    private val validPrivateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e493"

    @Before
    fun setUp() {
        validator = PrivateKeyValidator(textInputLayout, {})
        validator = Mockito.spy(validator)
        Mockito.doReturn("required").`when`(validator).getTextFromResource(R.string.error_private_key_required)
        Mockito.doReturn("format").`when`(validator).getTextFromResource(R.string.error_private_key_format)
        Mockito.doReturn("length").`when`(validator).getTextFromResource(R.string.error_private_key_length)
    }

    @Test
    fun invalid_null() {
        assertFalse(validator.isValid(null))
        assertEquals("required", validator.error)
    }

    @Test
    fun invalid_empty() {
        assertFalse(validator.isValid(""))
        assertEquals("required", validator.error)
    }

    @Test
    fun invalid_badFormat() {
        assertFalse(validator.isValid(badFormatPrivateKey))
        assertEquals("format", validator.error)
    }

    @Test
    fun invalid_badLength() {
        assertFalse(validator.isValid(badLengthPrivateKey))
        assertEquals("length", validator.error)
    }

    @Test
    fun shouldBeValid() {
        assertTrue(validator.isValid(validPrivateKey))
        assertNull(validator.error)
    }

}