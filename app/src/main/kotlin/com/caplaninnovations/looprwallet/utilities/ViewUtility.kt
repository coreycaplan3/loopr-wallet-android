package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by Corey on 1/17/2018.
 * Project: MeetUp
 * <p></p>
 * Purpose of Class:
 */
inline fun View.snack(@StringRes message: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes actionText: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionText, listener)
    color?.let { setActionTextColor(color) }
}