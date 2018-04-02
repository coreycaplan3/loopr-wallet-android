package org.loopring.looprwallet.core.utilities

import android.support.annotation.*
import org.loopring.looprwallet.core.application.LooprWalletApp

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 */
object ApplicationUtility {

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletApp] application instance
     */
    fun bool(@BoolRes resId: Int) = LooprWalletApp.application.resources.getBoolean(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletApp] application instance
     */
    fun int(@IntegerRes resId: Int) = LooprWalletApp.application.resources.getInteger(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletApp] application instance
     */
    fun str(@StringRes resId: Int) = LooprWalletApp.application.getString(resId)!!

    /**
     * @param resId The string resource
     * @return Gets a string array using the [LooprWalletApp] application instance
     */
    fun strArray(@ArrayRes resId: Int) = LooprWalletApp.application.resources.getStringArray(resId)!!


    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [LooprWalletApp] application instance
     */
    fun dimen(@DimenRes resId: Int) = LooprWalletApp.application.resources.getDimension(resId)

    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [LooprWalletApp] application instance
     */
    @Suppress("deprecation")
    fun color(@ColorRes resId: Int) = LooprWalletApp.application.resources.getColor(resId)

    @Suppress("deprecation")
    fun drawable(@DrawableRes resId: Int) = LooprWalletApp.application.resources.getDrawable(resId)

}