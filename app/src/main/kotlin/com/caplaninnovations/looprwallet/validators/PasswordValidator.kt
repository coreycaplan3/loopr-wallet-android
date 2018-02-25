package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 2/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param textInputLayout The [TextInputLayout] whose text will be monitored
 * @param onChangeListener A listener that will be invoked whenever the content of [TextInputLayout]
 * changes. Typically, this will be used to enable/disable the submit button.
 */
open class PasswordValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_password_required)
                return false
            }
            text.length < 9 -> {
                error = getTextFromResource(R.string.error_password_too_short)
                return false
            }
            text.length > 255 -> {
                error = getTextFromResource(R.string.error_too_long)
                return false
            }
            else -> {
                error = null
                true
            }
        }
    }

}