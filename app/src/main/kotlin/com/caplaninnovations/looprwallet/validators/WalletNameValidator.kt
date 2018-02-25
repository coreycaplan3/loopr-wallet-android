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
open class WalletNameValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_wallet_name_required)
                false
            }
            !text.matches(Regex("[a-zA-Z0-9\\-_]+")) -> {
                error = getTextFromResource(R.string.error_wallet_name_bad_format)
                false
            }
            text.length < 3 -> {
                error = getTextFromResource(R.string.error_wallet_name_too_short)
                false
            }
            text.length > 255 -> { // check against R.integer.wallet_name_max_length
                error = getTextFromResource(R.string.error_wallet_name_too_long)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}