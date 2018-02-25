package com.caplaninnovations.looprwallet.validators

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import org.web3j.crypto.WalletUtils
import org.web3j.utils.Numeric

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class PrivateKeyValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit,
        isRequired: Boolean = true
) : BaseValidator(textInputLayout, onChangeListener, isRequired) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_private_key_required)
                false
            }
            !isValidHex(text) -> {
                error = getTextFromResource(R.string.error_private_key_format)
                false
            }
            !WalletUtils.isValidPrivateKey(text) -> {
                error = getTextFromResource(R.string.error_private_key_length)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

    private fun isValidHex(text: String): Boolean {
        return try {
            Numeric.toBigIntNoPrefix(text)
            true
        } catch (e: Exception) {
            false
        }
    }

}