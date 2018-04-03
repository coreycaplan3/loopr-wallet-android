package org.loopring.looprwallet.core.validators

import android.support.design.widget.TextInputLayout
import org.loopring.looprwallet.core.R

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
        private val otherPassword: String,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                // The passwords cannot match anyway, since the it's empty.
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