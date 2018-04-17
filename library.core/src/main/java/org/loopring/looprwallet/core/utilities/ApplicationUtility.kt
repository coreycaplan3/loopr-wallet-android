package org.loopring.looprwallet.core.utilities

import android.support.annotation.*
import org.loopring.looprwallet.core.application.CoreLooprWalletApp

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
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun bool(@BoolRes resId: Int) = CoreLooprWalletApp.application.resources.getBoolean(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun int(@IntegerRes resId: Int) = CoreLooprWalletApp.application.resources.getInteger(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun str(@StringRes resId: Int) = CoreLooprWalletApp.application.getString(resId)!!

    /**
     * @param resId The string resource
     * @return Gets a string array using the [CoreLooprWalletApp] application instance
     */
    fun strArray(@ArrayRes resId: Int) = CoreLooprWalletApp.application.resources.getStringArray(resId)!!


    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [CoreLooprWalletApp] application instance
     */
    fun dimen(@DimenRes resId: Int) = CoreLooprWalletApp.application.resources.getDimension(resId)

    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [CoreLooprWalletApp] application instance
     */
    @Suppress("deprecation")
    fun color(@ColorRes resId: Int) = CoreLooprWalletApp.application.resources.getColor(resId)

    @Suppress("deprecation")
    fun drawable(@DrawableRes resId: Int) = CoreLooprWalletApp.application.resources.getDrawable(resId)

}