package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import org.web3j.crypto.WalletUtils

/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class PublicKeyValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_public_key_required)
                false
            }
            !text.toLowerCase().matches(Regex("0x[a-f0-9]+")) -> {
                error = getTextFromResource(R.string.error_public_key_format)
                false
            }
            !WalletUtils.isValidAddress(text) -> {
                error = getTextFromResource(R.string.error_public_key_length)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}