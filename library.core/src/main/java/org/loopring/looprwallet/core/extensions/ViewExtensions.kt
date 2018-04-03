package org.loopring.looprwallet.core.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.*
import android.support.design.widget.BaseTransientBottomBar.Duration
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

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
 * A utility function for creating and showing a long toast
 *
 * @param stringRes A string resource that is used for the message of the toast
 */
fun Context.longToast(@StringRes stringRes: Int) {
    Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()
}

/**
 * A utility function for creating and showing a long toast
 *
 * @param message A string that is used for the message of the toast
 */
fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Sets up this [RecyclerView] to forward scroll events to the [FloatingActionButton]. The result
 * is the [FloatingActionButton] will show or hide depending on the scroll direction.
 */
fun RecyclerView.setupWithFab(fab: FloatingActionButton) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            if (dy < 0 && !fab.isShown)
                fab.show()
            else if (dy > 0 && fab.isShown)
                fab.hide()
        }
    })
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
 * @param layoutRes The layout resource that will be inflated
 * @param attachToRoot True to attach it to the root or false to not attach it.
 * @return The view that was inflated if [attachToRoot] was false or the root view if [attachToRoot]
 * was set to true.
 */
fun ViewGroup?.inflate(context: Context, @LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
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

// SNACKBAR stuff

private val snackbarMap = HashMap<String, Boolean?>()

/**
 * @param text The text displayed in the snackbar, which is used as a key to find it.
 * @return True if the provided snackbar is visible or false otherwise.
 */
fun isSnackbarVisible(text: String) = snackbarMap[text] == true

/**
 * Creates a snackbar by using this view to instantiate it and automatically calls show
 *
 * @param message The string is used to display the snackbar's message
 * @param length The length that the snackbar will be displayed. Defaults to [Snackbar.LENGTH_LONG]
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
@SuppressLint("Range")
fun View.snackbar(message: String, @Duration length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, length)
            .addCallback(getSnackbarCallback(message))
            .show()
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
@SuppressLint("Range")
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
 * Creates a long snackbar by using this view to instantiate it and *automatically* call
 * [Snackbar.show].
 *
 * @param message The string resource that is used to display the snackbar's message
 * @param actionText The string resource that is used to display the snackbar's action
 * @param listener The listener that will be called if the action button is clicked
 * @see View.snackbar
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
fun View.longSnackbarWithAction(@StringRes message: Int,
                                @StringRes actionText: Int,
                                listener: (View) -> Unit): Snackbar {
    return snackbarWithAction(message, Snackbar.LENGTH_LONG, actionText, null, listener)
}

/**
 * Creates an indefinite snackbar by using this view to instantiate it and *automatically* call
 * [Snackbar.show].
 *
 * @param message The string resource that is used to display the snackbar's message
 * @param actionText The string resource that is used to display the snackbar's action
 * @param listener The listener that will be called if the action button is clicked
 * @see View.snackbar
 * @see Snackbar.LENGTH_SHORT
 * @see Snackbar.LENGTH_LONG
 * @see Snackbar.LENGTH_INDEFINITE
 */
fun View.indefiniteSnackbarWithAction(@StringRes message: Int,
                                      @StringRes actionText: Int,
                                      listener: (View) -> Unit): Snackbar {
    return snackbarWithAction(message, Snackbar.LENGTH_INDEFINITE, actionText, null, listener)
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
                            @ColorInt color: Int? = null,
                            listener: (View) -> Unit): Snackbar {
    val snackbar = Snackbar.make(this, str(message), length)
            .setAction(actionText, listener)
            .addCallback(getSnackbarCallback(str(message)))

    color?.let { snackbar.setActionTextColor(color) }

    snackbar.show()

    return snackbar
}

private fun getSnackbarCallback(message: String) = object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
    override fun onShown(transientBottomBar: Snackbar?) {
        super.onShown(transientBottomBar)
        snackbarMap[message] = true
    }

    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
        super.onDismissed(transientBottomBar, event)
        snackbarMap.remove(message)
    }
}