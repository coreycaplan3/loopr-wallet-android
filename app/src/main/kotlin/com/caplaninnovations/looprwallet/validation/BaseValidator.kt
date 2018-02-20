package com.caplaninnovations.looprwallet.validation

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
abstract class BaseValidator(private val textInputLayout: TextInputLayout) : TextWatcher {

    /**
     * It's initialized to a nonnull value, so [isValid] returns false
     */
    protected var error: String? = ""

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
    }

    /**
     * Checks if a given string is valid. If it's not valid, [error] should be set to a nonnull
     * value and this method should return false. If it *is* valid, [error] should be set to null
     * and this method should return true.
     */
    protected abstract fun isValid(text: String?): Boolean

    fun isValid(): Boolean = error != null

    fun getText() = textInputLayout.editText?.text?.toString()

    fun destroy() {
        textInputLayout.editText?.removeTextChangedListener(this)
    }
}