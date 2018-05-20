package org.loopring.looprwallet.core.utilities

import android.content.Context
import android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.extensions.isJellybeanR1
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.drawable


/**
 * Created by Corey Caplan on 3/16/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object ViewUtility {

    fun closeKeyboard(view: View) {
        val manager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isRtl(): Boolean {
        return if (isJellybeanR1()) {
            CoreLooprWalletApp.context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        } else {
            false
        }
    }

    fun getNavigationIcon(@DrawableRes drawableRes: Int, theme: Resources.Theme): Drawable {
        val drawable = drawable(drawableRes, CoreLooprWalletApp.context)
        DrawableCompat.setTint(drawable, col(theme.getResourceIdFromAttrId(R.attr.titleTextColor)))
        return drawable
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    fun isXLargeTablet(): Boolean {
        val screenLayout = CoreLooprWalletApp.context.resources.configuration.screenLayout
        return screenLayout and SCREENLAYOUT_SIZE_MASK >= SCREENLAYOUT_SIZE_XLARGE
    }

}