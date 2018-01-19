package com.caplaninnovations.looprwallet.utilities

import android.content.Context
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
inline fun Context.getAttrColor(@AttrRes attrRes: Int, f: Context.() -> Unit = {}): Int {
    f()

    val typedValue = TypedValue()

    if(!theme.resolveAttribute(attrRes, typedValue, true)) {
        loge("Could not resolve color for attribute $attrRes")
        return -1
    }

    return typedValue.data
}