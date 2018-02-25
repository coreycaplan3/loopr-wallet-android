package com.caplaninnovations.looprwallet.validators

import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import com.caplaninnovations.looprwallet.utilities.str

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param textInputLayout The [TextInputLayout] whose text will be monitored
 * @param onChangeListener A listener that will be invoked whenever the content of [TextInputLayout]
 * changes. Typically, this will be used to enable/disable the submit button.
 * @param isRequired True if the field is required (cannot be empty) or false otherwise.
 */
abstract class BaseValidator(
        private val textInputLayout: TextInputLayout,
        private val onChangeListener: () -> Unit,
        private val isRequired: Boolean = true
) : TextWatcher {

    companion object {
        const val DEFAULT_ERROR = "error"
    }

    /**
     * If this validator's field is required, it's initialized to a nonnull value, so [isValid]
     * returns false for empty/default states.
     *
     * Otherwise, it is initialized to null, so it returns true initially for default/empty states.
     */
    @VisibleForTesting
    var error: String? = if (isRequired) DEFAULT_ERROR else null

    init {
        textInputLayout.editText?.addTextChangedListener(this)

        textInputLayout.isErrorEnabled = true
        textInputLayout.isHintEnabled = true
    }

    final override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    final override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    final override fun afterTextChanged(s: Editable?) {
        if (isValid(s?.toString())) {
            textInputLayout.error = null
        } else {
            textInputLayout.error = error
        }

        onChangeListener.invoke()
    }

    /**
     * Checks if a given string is valid. If it's not valid, [error] should be set to a nonnull
     * value and this method should return false. If it *is* valid, [error] should be set to null
     * and this method should return true.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    abstract fun isValid(text: String?): Boolean

    /**
     * @return True if the input is valid or false otherwise
     */
    fun isValid(): Boolean = error == null

    /**
     * @return The current text that the user inputted into the [TextInputLayout]
     */
    fun getInputText() = getTextFromInputLayout()

    fun destroy() {
        textInputLayout.editText?.removeTextChangedListener(this)
    }

    // Mark - Private Methods

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    open fun getTextFromInputLayout(): String? {
        return textInputLayout.editText?.text?.toString()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    open fun getTextFromResource(@StringRes resource: Int): String? {
        return str(resource)
    }

}