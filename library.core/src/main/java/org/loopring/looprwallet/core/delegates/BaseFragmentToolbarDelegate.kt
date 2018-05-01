package org.loopring.looprwallet.core.delegates

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.utilities.ViewUtility

/**
 * Created by Corey on 5/1/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
open class BaseFragmentToolbarDelegate(
        savedInstanceState: Bundle?,
        isToolbarCollapseEnabled: Boolean = false,
        baseFragment: BaseFragment
) {

    companion object {

        private const val KEY_IS_TOOLBAR_COLLAPSED = "_IS_TOOLBAR_COLLAPSED"
    }

    protected val activity by weakReference(baseFragment.activity)
    protected val fragment by weakReference(baseFragment)

    var isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED)
            ?: isToolbarCollapseEnabled

    var toolbar: Toolbar? = null
        private set

    var appbarLayout: AppBarLayout? = null
        private set

    private var isUpNavigationEnabled = true

    private val parentFragment
        get() = fragment?.parentFragment

    fun setupAppbar(fragmentView: ViewGroup) {
        appbarLayout = fragment?.createAppbarLayout(fragmentView)
        fragmentView.addView(appbarLayout, 0)
        toolbar = appbarLayout?.findViewById(R.id.toolbar)

        val theme = activity?.theme
        if (isUpNavigationEnabled && theme != null) {
            logi("Up navigation is enabled. Setting up...")
            toolbar?.navigationIcon = ViewUtility.getNavigationIcon(R.drawable.ic_forward_white_24dp, theme)
            toolbar?.setNavigationContentDescription(R.string.content_description_navigation_icon)
            toolbar?.setNavigationOnClickListener(onNavigationClick)
        }

        invalidateOptionsMenu()

        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing()
        } else {
            disableToolbarCollapsing()
        }
    }

    fun invalidateOptionsMenu() {
        onCreateOptionsMenu?.invoke(toolbar)
        toolbar?.setOnMenuItemClickListener(onOptionsItemSelected)
    }

    var onNavigationClick: ((View) -> Unit) = {
        activity?.onBackPressed()
    }

    var onCreateOptionsMenu: ((Toolbar?) -> Unit)? = null

    var onOptionsItemSelected: ((MenuItem?) -> Boolean) = {
        when {
            it?.itemId == android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            it?.itemId == R.id.menuMainSettings -> {
                activity?.let { it.startActivity(Intent(it, SettingsActivity::class.java)) }
                true
            }
            else -> false
        }
    }

    /**
     * Enables the toolbar to be collapsed when scrolling
     */
    fun enableToolbarCollapsing() {
        if (parentFragment != null) {
            // Children don't have toolbars
            return
        }

        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        }

        val container = fragment?.view?.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is put underneath the toolbar since it is going to be moved out of the
            // way after scrolling
            logd("Setting container layout params to enable scrolling behavior...")
            it.topMargin = 0
            it.behavior = AppBarLayout.ScrollingViewBehavior()
            container.requestLayout()
        }

        isToolbarCollapseEnabled = true
    }

    /**
     * Disables the toolbar from being collapsed when scrolling
     */
    fun disableToolbarCollapsing() {
        if (parentFragment != null) {
            // Children don't have toolbars
            return
        }

        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = 0
        }

        val view = fragment?.view
        if (view == null) {
            loge("BaseView was null!", IllegalStateException())
            return
        }

        val container = view.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is underneath the toolbar, so we must add margin so it is below it instead
            val topMarginResource = fragment?.context?.theme?.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            if (topMarginResource != null) {
                it.topMargin = dimen(topMarginResource).toInt()
            }

            it.behavior = null
            container.requestLayout()
        }

        isToolbarCollapseEnabled = false
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
    }

}