package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import com.caplaninnovations.looprwallet.application.LooprWalletApp

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 *
 * TODO ADD MORE RESOURCE EASY RESOURCE RETRIEVAL
 */
object ApplicationUtility {

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletApp] application instance
     */
    fun str(@StringRes resId: Int): String = LooprWalletApp.application.getString(resId)

    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [LooprWalletApp] application instance
     */
    fun dimen(@DimenRes resId: Int): Float = LooprWalletApp.application.resources.getDimension(resId)

}