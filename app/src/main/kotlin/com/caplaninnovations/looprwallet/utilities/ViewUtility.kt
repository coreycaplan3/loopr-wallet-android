package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_markets_parent.*

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
fun Context.shortToast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

fun View.setBackgroundTint(@ColorInt tintColor: Int) {
    background?.let {
        val drawable = DrawableCompat.wrap(it)
        DrawableCompat.setTint(drawable, tintColor)
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_OVER)
    }
}

fun View.setPaddingTop(value: Int) {
    setPadding(paddingLeft, value, paddingRight, paddingBottom)
}

fun <T : View> View.findById(@IdRes id: Int): T? {
    return this.findViewById(id)
}

fun TabLayout.forEachTab(f: (tab: TabLayout.Tab) -> Void) {
    for (i in 0..this.tabCount) {
        f(getTabAt(i)!!)
    }
}

fun TabLayout.findTabByTag(tag: String): TabLayout.Tab? {
    for (i in 0..this.tabCount) {
        val tab = this.getTabAt(i)
        val isTabFound = tab?.tag?.equals(tag) ?: false
        if (isTabFound) return tab
    }

    return null
}

inline fun View.snack(@StringRes message: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes actionText: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionText, listener)
    color?.let { setActionTextColor(color) }
}