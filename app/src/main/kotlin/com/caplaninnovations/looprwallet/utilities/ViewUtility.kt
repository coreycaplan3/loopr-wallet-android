package com.caplaninnovations.looprwallet.utilities

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.*
import android.support.design.widget.BaseTransientBottomBar.Duration
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.caplaninnovations.looprwallet.R

/**
 * Created by Corey on 1/17/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

object ViewUtility {

    /**
     * Closes the soft keyboard, if open.
     *
     * @param view A view that is currently in the hierarchy
     */
    fun closeKeyboard(view: View) {
        val manager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromInputMethod(view.windowToken, 0)
    }
}

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
 * A utility function for creating a long toast
 *
 * @param stringResource A string resource that is used for the message of the toast
 */
fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * @param layoutRes The layout resource that will be inflated
 * @param attachToRoot True to attach it to the root or false to not attach it.
 * @return The view that was inflated if [attachToRoot] was false or the root view if [attachToRoot]
 * was set to true.
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
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

fun AppBarLayout.isExpanded(): Boolean {
    val coordinatorLayoutParams = (layoutParams as? CoordinatorLayout.LayoutParams)
    val behavior = (coordinatorLayoutParams?.behavior as? AppBarLayout.Behavior)
    return behavior?.topAndBottomOffset == 0
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
 * Creates a snackbar with an error message attached to it. This view is used to instantiate it,
 * and show is automatically called.
 */
fun View.snackbarUnknownError() {
    this.snackbar(str(R.string.error_unknown))
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
                            length: Int = Snackbar.LENGTH_LONG,
                            @StringRes actionText: Int,
                            listener: (View) -> Unit,
                            @ColorInt color: Int? = null) {
    val snackbar = Snackbar.make(this, str(message), length)
            .setAction(actionText, listener)

    color?.let { snackbar.setActionTextColor(color) }

    snackbar.show()
}