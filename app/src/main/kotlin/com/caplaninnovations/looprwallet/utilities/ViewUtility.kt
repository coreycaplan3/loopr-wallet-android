package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.isJellybeanR1


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
            LooprWalletApp.context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        } else {
            false
        }
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    fun isXLargeTablet(): Boolean {
        val screenLayout = LooprWalletApp.context.resources.configuration.screenLayout
        return screenLayout and SCREENLAYOUT_SIZE_MASK >= SCREENLAYOUT_SIZE_XLARGE
    }

}