package org.loopring.looprwallet.core.validators

import android.support.design.widget.TextInputLayout
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.utilities.RegexUtility

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
open class TokenDecimalValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_token_decimal_required)
                false
            }
            !text.matches(RegexUtility.TOKEN_TICKER_REGEX) -> {
                error = getTextFromResource(R.string.error_token_decimal_bad_format)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}