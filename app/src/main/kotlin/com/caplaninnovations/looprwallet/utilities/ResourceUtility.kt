package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.TypedValue

/**
 * Created by Corey on 1/18/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
fun Context.getResourceIdFromAttrId(@AttrRes attrRes: Int): Int {
    val outValue = TypedValue() // this value is mutated in the call to "resolveAttribute"

    if (!theme.resolveAttribute(attrRes, outValue, true)) {
        loge("Could not resolve dimen for attribute $attrRes")
        return -1
    }

    return outValue.resourceId
}

fun Context.getAttrColorStateList(@AttrRes attrRes: Int): ColorStateList? {
    val resourceId = getResourceIdFromAttrId(attrRes)

    @Suppress("DEPRECATION")
    if (isMarshmallow()) {
        return resources.getColorStateList(resourceId, theme)
    } else {
        return resources.getColorStateList(resourceId)
    }
}