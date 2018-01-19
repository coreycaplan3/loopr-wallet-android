package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.view.View

/**
 * Created by Corey on 1/17/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
fun View.setPaddingTop(value: Int) {
    setPadding(paddingLeft, value, paddingRight, paddingBottom)
}

fun <T : View> View.findById(@IdRes id: Int): T? {
    return findViewById(id) as? T
}

fun TabLayout.findTabByTag(tag: String): TabLayout.Tab? {
    for (i in 0.rangeTo(this.tabCount)) {
        val tab = this.getTabAt(i)
        val isTabFound = tab?.tag?.equals(tag) ?: false
        if(isTabFound) return tab
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