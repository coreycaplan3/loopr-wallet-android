package org.loopring.looprwallet.core.models.error

import android.support.annotation.StringRes
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param errorMessage The message that can be displayed to the user
 * @param errorType The type of error.
 * @see ErrorTypes
 * @see ErrorTypes.NO_CONNECTION
 * @see ErrorTypes.UNKNOWN
 * @see ErrorTypes.SERVER_COMMUNICATION_ERROR
 * @see ErrorTypes.SERVER_ERROR
 */
open class LooprError(@StringRes val errorMessage: Int, val errorType: Int) {

    companion object {

        val UNKNOWN_ERROR = LooprError(R.string.error_unknown, ErrorTypes.UNKNOWN)

    }

}