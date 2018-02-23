package com.caplaninnovations.looprwallet.validation

import android.support.design.widget.TextInputLayout
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.str
import org.web3j.crypto.WalletUtils
import java.math.BigInteger

/**
 * Created by Corey Caplan on 2/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class PrivateKeyValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit,
        isRequired: Boolean = true
) : BaseValidator(textInputLayout, onChangeListener, isRequired) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null -> {
                error = str(R.string.error_private_key_required)
                false
            }
            isValidPrivateKey(text) -> {
                error = null
                true
            }
            else -> {
                error = str(R.string.error_invalid_private_key)
                false
            }
        }
    }

    private fun isValidPrivateKey(text: String): Boolean {
        return try {
            BigInteger(text, 16)
            WalletUtils.isValidPrivateKey(text)
        } catch (e: Exception) {
            false
        }
    }

}