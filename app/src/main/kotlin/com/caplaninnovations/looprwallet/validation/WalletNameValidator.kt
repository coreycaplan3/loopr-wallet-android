package com.caplaninnovations.looprwallet.validation

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
 */
class WalletNameValidator(textInputLayout: TextInputLayout) : BaseValidator(textInputLayout) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = str(R.string.error_wallet_name_required)
                false
            }
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