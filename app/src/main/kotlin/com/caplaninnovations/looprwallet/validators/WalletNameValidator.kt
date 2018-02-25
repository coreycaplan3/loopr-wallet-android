package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.str

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 * @param textInputLayout The [TextInputLayout] whose text will be monitored
 * @param onChangeListener A listener that will be invoked whenever the content of [TextInputLayout]
 * changes. Typically, this will be used to enable/disable the submit button.
 */
class WalletNameValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = str(R.string.error_wallet_name_required)
                false
            }
            // TODO length edge cases and numbers in regex
            !text.matches(Regex("[a-zA-Z\\-_]+")) -> {
                error = str(R.string.error_wallet_name_bad_format)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}