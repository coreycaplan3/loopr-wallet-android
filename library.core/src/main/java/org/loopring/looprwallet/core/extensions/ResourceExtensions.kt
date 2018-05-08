package org.loopring.looprwallet.core.extensions

import android.content.res.ColorStateList
import android.content.res.Resources
import android.support.annotation.AttrRes
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import org.loopring.looprwallet.core.application.CoreLooprWalletApp

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * @param px The amount, in pixels, that will be converted to Dp.
 *
 * @return The inputted pixel value, represented as density-independent pixels.
 */
fun Resources.pixelsToDp(px: Float): Float {
    return px / (this.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * @return The resource ID from a given attribute. For example, passing
 * [android.R.attr.actionBarSize] returns a resource that can be used with *Resources.getDimen*
 * to get the action bar's size.
 */
fun Resources.Theme.getResourceIdFromAttrId(@AttrRes attrRes: Int): Int {
    val outValue = TypedValue() // this value is mutated in the call to "resolveAttribute"

    if (!resolveAttribute(attrRes, outValue, true)) {
        loge("Could not resolve dimen for attribute $attrRes")
        return -1
    }

    return outValue.resourceId
}

/**
 * @return A color state list if the given attribute resource is valid, or null otherwise
 */
fun Resources.Theme.getAttrColorStateList(@AttrRes attrRes: Int): ColorStateList? {
    val resourceId = getResourceIdFromAttrId(attrRes)
    return ResourcesCompat.getColorStateList(CoreLooprWalletApp.context.resources, resourceId, this)
}