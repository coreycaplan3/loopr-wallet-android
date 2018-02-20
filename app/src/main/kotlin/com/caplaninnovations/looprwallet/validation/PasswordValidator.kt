package com.caplaninnovations.looprwallet.validation

import android.support.annotation.StringRes

/**
 * Created by Corey Caplan on 2/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class PasswordValidator(val password: String) {

    private var passwordError: Int? = null

    fun isPasswordValid(): Boolean {
        return true
    }

    /**
     * Gets the error
     */
    @StringRes
    fun getErrorDescription(): Int? {
        return if (!isPasswordValid()) {
            passwordError
        } else {
            null
        }
    }

}