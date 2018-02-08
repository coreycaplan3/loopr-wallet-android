package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.TypedValue

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
 * @return The resource ID from a given attribute. For example, passing
 * [android.R.attr.actionBarSize] returns a resource that can be used with [Resources.getDimen]
 * to get the action bar's size
 */
fun Context.getResourceIdFromAttrId(@AttrRes attrRes: Int): Int {
    val outValue = TypedValue() // this value is mutated in the call to "resolveAttribute"

    if (!theme.resolveAttribute(attrRes, outValue, true)) {
        loge("Could not resolve dimen for attribute $attrRes")
        return -1
    }

    return outValue.resourceId
}

/**
 * @return A color state list if the given attribute resource is valid, or null otherwise
 */
fun Context.getAttrColorStateList(@AttrRes attrRes: Int): ColorStateList? {
    val resourceId = getResourceIdFromAttrId(attrRes)

    @Suppress("DEPRECATION")
    return if (isMarshmallow()) {
        resources.getColorStateList(resourceId, theme)
    } else {
        resources.getColorStateList(resourceId)
    }
}