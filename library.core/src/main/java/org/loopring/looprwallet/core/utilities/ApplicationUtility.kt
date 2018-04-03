package org.loopring.looprwallet.core.utilities

import android.support.annotation.*
import org.loopring.looprwallet.core.application.LooprWalletCoreApp

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
     * @return Gets a string using the [LooprWalletCoreApp] application instance
     */
    fun bool(@BoolRes resId: Int) = LooprWalletCoreApp.application.resources.getBoolean(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletCoreApp] application instance
     */
    fun int(@IntegerRes resId: Int) = LooprWalletCoreApp.application.resources.getInteger(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [LooprWalletCoreApp] application instance
     */
    fun str(@StringRes resId: Int) = LooprWalletCoreApp.application.getString(resId)!!

    /**
     * @param resId The string resource
     * @return Gets a string array using the [LooprWalletCoreApp] application instance
     */
    fun strArray(@ArrayRes resId: Int) = LooprWalletCoreApp.application.resources.getStringArray(resId)!!


    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [LooprWalletCoreApp] application instance
     */
    fun dimen(@DimenRes resId: Int) = LooprWalletCoreApp.application.resources.getDimension(resId)

    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [LooprWalletCoreApp] application instance
     */
    @Suppress("deprecation")
    fun color(@ColorRes resId: Int) = LooprWalletCoreApp.application.resources.getColor(resId)

    @Suppress("deprecation")
    fun drawable(@DrawableRes resId: Int) = LooprWalletCoreApp.application.resources.getDrawable(resId)

}