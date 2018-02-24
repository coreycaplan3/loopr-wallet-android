package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey Caplan on 2/24/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(MockitoJUnitRunner::class)
class BaseValidatorTest {

    private val defaultText = "hello"
    private val defaultError = "hello-error"

    @Mock
    lateinit var textInputLayout: TextInputLayout

    lateinit var successValidator: BaseValidator
    lateinit var failureValidator: BaseValidator

    var isChangeCalled = false

    @Before
    fun setUp() {
        successValidator = object : BaseValidator(
                textInputLayout,
                this@BaseValidatorTest::onTextChanged,
                isRequired = true
        ) {
            override fun isValid(text: String?): Boolean {
                error = null
                return true
            }
        }

        failureValidator = object : BaseValidator(
                textInputLayout,
                this@BaseValidatorTest::onTextChanged,
                isRequired = true
        ) {
            override fun isValid(text: String?): Boolean {
                error = defaultError
                return false
            }
        }

        successValidator = Mockito.spy(successValidator)

        Mockito.`when`(successValidator.getTextFromInputLayout()).thenReturn(defaultText)
    }

    @After
    fun tearDown() {
        successValidator.destroy()
    }

    @Test
    fun isValid_initial_isRequiredField() {
        // The initial state is invalid, since the field is required and text is not initialized
        assertFalse(successValidator.isValid())
    }

    @Test
    fun isValid_afterTextChanged() {
        assertTrue(successValidator.isValid())
    }

    @Test
    fun isValid_initial_isNotRequiredField() {
        val notRequiredValidator = object : BaseValidator(textInputLayout, { onTextChanged() }, false) {
            override fun isValid(text: String?): Boolean = false
        }

        // The initial state is valid, since the field is not required
        assertTrue(notRequiredValidator.isValid())
    }

    @Test
    fun getText() {
        assertEquals(defaultText, successValidator.getText())
    }

    private fun onTextChanged() {
        isChangeCalled = true
    }

}