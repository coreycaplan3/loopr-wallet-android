package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.BaseTransientBottomBar.Duration
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.Toast

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * A utility function for creating a short toast
 *
 * @param stringResource A string resource that is used for the message of the toast
 */
fun Context.shortToast(@StringRes stringResource: Int) {
    Toast.makeText(this, stringResource, Toast.LENGTH_SHORT).show()
}

/**
 * A utility function for creating a long toast
 *
 * @param stringResource A string resource that is used for the message of the toast
 */
fun Context.longToast(@StringRes stringResource: Int) {
    Toast.makeText(this, stringResource, Toast.LENGTH_LONG).show()
}

/**
 * Sets the background tint of this view, by getting the background drawable and using
 * [DrawableCompat] to set it. The tint mode used is [PorterDuff.Mode.SRC_OVER].
 *
 * @param tintColor The resource that points to the color used for tinting the background
 */
fun View.setBackgroundTint(@ColorInt tintColor: Int) {
    background?.let {
        val drawable = DrawableCompat.wrap(it)
        DrawableCompat.setTint(drawable, tintColor)
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_OVER)
    }
}

/**
 * Sets the top padding of this view in (in pixels).
 *
 * @param value The value to which this view's top padding will be set. Must be in pixels.
 */
fun View.setPaddingTop(value: Int) {
    setPadding(paddingLeft, value, paddingRight, paddingBottom)
}

/**
 * Finds a tab in this [TabLayout] by its tag
 *
 * @param tag The tag trying to be found in this tab layout.
 * @return The [TabLayout.Tab] if the tag was found or null otherwise
 */
fun TabLayout.findTabByTag(tag: String): TabLayout.Tab? {
    return (0..this.tabCount)
            .map { this.getTabAt(it) }
            .firstOrNull { it?.tag == tag }
}

/**
 * Creates a snackbar by using this view to instantiate it and automatically calls show
 *
 * @param message The string is used to display the snackbar's message
 * @param length The length that the snackbar will be displayed. Defaults to [Snackbar.LENGTH_LONG]
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
fun View.snackbar(message: String, @Duration length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, length).show()
}

/**
 * Creates a snackbar by using this view to instantiate it and automatically calls show
 *
 * @param message The string resource that is used to display the snackbar's message
 * @param length The length that the snackbar will be displayed. Defaults to [Snackbar.LENGTH_LONG]
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
fun View.snackbar(@StringRes message: Int, @Duration length: Int = Snackbar.LENGTH_LONG) {
    this.snackbar(str(message), length)
}

/**
 * Creates a snackbar by using this view to instantiate it and automatically calls show
 *
 * @param message The string resource that is used to display the snackbar's message
 * @param length The length that the snackbar will be displayed. Defaults to [Snackbar.LENGTH_LONG]
 * @param actionText The string resource that is used to display the snackbar's action
 * @param listener The listener that will be called if the action button is clicked
 * @param color The color that will be used for the snackbar's action button. Defaults to the
 * current theme's accent color
 * @see View.snackbar
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
fun View.snackbarWithAction(@StringRes message: Int,
                            @Duration length: Int = Snackbar.LENGTH_LONG,
                            @StringRes actionText: Int,
                            listener: (View) -> Unit,
                            @ColorInt color: Int? = null) {
    val snackbar = Snackbar.make(this, str(message), length)
            .setAction(actionText, listener)

    color?.let { snackbar.setActionTextColor(color) }

    snackbar.show()
}