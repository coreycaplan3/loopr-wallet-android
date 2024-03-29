package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 2/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To check the [otherPassword] against the inputted one to see if it's valid
 *
 */
open class PasswordMatcherValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit,
        private val otherPassword: String
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                // The passwords cannot match anyway, since the length is less than the min.
                error = null
                false
            }
            text.length < 9 -> {
                error = getTextFromResource(R.string.error_password_too_short)
                false
            }
            text != otherPassword -> {
                error = getTextFromResource(R.string.error_password_incorrect)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }
}