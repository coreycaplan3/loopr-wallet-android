package org.loopring.looprwallet.core.validators

import android.support.design.widget.TextInputLayout
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.utilities.RegexUtility

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
open class ContactNameValidator(
        textInputLayout: TextInputLayout,
        onChangeListener: () -> Unit
) : BaseValidator(textInputLayout, onChangeListener) {

    private val nameMaxLength = CoreLooprWalletApp.context.resources.getInteger(R.integer.name_max_length)

    override fun isValid(text: String?): Boolean {
        return when {
            text == null || text.isEmpty() -> {
                error = getTextFromResource(R.string.error_contact_name_required)
                false
            }
            !text.matches(RegexUtility.NAME_REGEX) -> {
                error = getTextFromResource(R.string.error_contact_name_bad_format)
                false
            }
            text.length > nameMaxLength -> {
                error = getTextFromResource(R.string.error_contact_name_too_long)
                false
            }
            else -> {
                error = null
                true
            }
        }
    }

}