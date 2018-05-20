package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
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

    private val application by lazy {
        CoreLooprWalletApp.application
    }

    /**
     * @param resId The string resource
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun bool(@BoolRes resId: Int) = application.resources.getBoolean(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun int(@IntegerRes resId: Int) = application.resources.getInteger(resId)

    /**
     * @param resId The string resource
     * @return Gets a string using the [CoreLooprWalletApp] application instance
     */
    fun str(@StringRes resId: Int): String = application.getString(resId)!!

    /**
     * @param resId The string resource
     * @return Gets a string array using the [CoreLooprWalletApp] application instance
     */
    fun strArray(@ArrayRes resId: Int): Array<String> = application.resources.getStringArray(resId)!!

    /**
     * @param resId The dimension resource
     * @return Gets a dimension using the [CoreLooprWalletApp] application instance
     */
    fun dimen(@DimenRes resId: Int) = application.resources.getDimension(resId)

    /**
     * @param resId The color resource
     * @return Gets a color using the [CoreLooprWalletApp] application instance
     */
    @Suppress("deprecation")
    fun col(@ColorRes @AttrRes resId: Int, context: Context? = null): Int {
        return when {
            context != null ->
                ContextCompat.getColor(context, resId)

            else ->
                application.resources.getColor(resId)
        }
    }

    /**
     * @return The drawable that is mapped to by the given [resId].
     */
    fun drawable(@DrawableRes resId: Int, context: Context): Drawable = ContextCompat.getDrawable(context, resId)!!

}