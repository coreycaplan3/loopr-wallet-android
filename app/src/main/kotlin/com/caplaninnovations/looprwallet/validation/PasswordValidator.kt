package com.caplaninnovations.looprwallet.validation

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
 */
class PasswordValidator(textInputLayout: TextInputLayout) : BaseValidator(textInputLayout) {

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