package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import android.text.SpannableStringBuilder
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

    open class BaseValidatorForTest(
            textInputLayout: TextInputLayout,
            onChangeListener: () -> Unit,
            isRequired: Boolean,
            private val defaultError: String?
    ) : BaseValidator(textInputLayout, onChangeListener, isRequired) {

        override fun isValid(text: String?): Boolean {
            error = defaultError
            return error == null
        }

    }

    private val defaultText = "hello"
    private val defaultError = "hello-error"

    @Mock
    lateinit var textInputLayout: TextInputLayout

    private lateinit var successValidator: BaseValidator
    private lateinit var failureValidator: BaseValidator

    private var isChangeCalled = false

    @Before
    fun setUp() {
        successValidator = BaseValidatorForTest(
                textInputLayout,
                this@BaseValidatorTest::onTextChanged,
                isRequired = true,
                defaultError = null
        )

        failureValidator = BaseValidatorForTest(
                textInputLayout,
                this@BaseValidatorTest::onTextChanged,
                isRequired = true,
                defaultError = defaultError
        )

        successValidator = Mockito.spy(successValidator)
        Mockito.doReturn(defaultText).`when`(successValidator).getTextFromInputLayout()

        failureValidator = Mockito.spy(failureValidator)
        Mockito.doReturn(defaultText).`when`(failureValidator).getTextFromInputLayout()
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
    fun isValid_initial_isNotRequiredField() {
        val notRequiredValidator = object : BaseValidator(textInputLayout, { onTextChanged() }, false) {
            override fun isValid(text: String?): Boolean = false
        }

        // The initial state is valid, since the field is not required
        assertTrue(notRequiredValidator.isValid())
    }

    @Test
    fun isValid_afterTextChanged() {
        successValidator.afterTextChanged(SpannableStringBuilder().append(defaultText))
        assertTrue(successValidator.isValid())
        assertTrue(isChangeCalled)
    }

    @Test
    fun isNotValid_afterTextChanged() {
        failureValidator.afterTextChanged(SpannableStringBuilder().append(defaultText))
        assertFalse(failureValidator.isValid())
        assertTrue(isChangeCalled)
    }

    @Test
    fun getText() {
        assertEquals(defaultText, successValidator.getText())
        assertEquals(defaultText, failureValidator.getText())
    }

    private fun onTextChanged() {
        isChangeCalled = true
    }

}