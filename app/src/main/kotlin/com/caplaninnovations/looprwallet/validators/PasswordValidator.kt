package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.str

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
class PasswordValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null -> {
                error = str(R.string.error_password_required)
                return false
            }
            text.length < 9 -> {
                error = str(R.string.error_password_too_short)
                return false
            }
            text.length > 255 -> {
                error = str(R.string.error_too_long)
                return false
            }
            else -> {
                error = null
                true
            }
        }
    }

}