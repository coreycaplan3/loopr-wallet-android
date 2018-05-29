package org.loopring.looprwallet.core.delegates

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.view.forEach
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
        private val fragment: BaseFragment
) {

    companion object {

        private const val KEY_IS_TOOLBAR_COLLAPSED = "_IS_TOOLBAR_COLLAPSED"
    }

    /**
     * Set to the opposite of the initial so it can be set in the beginning (the IF statement
     * doesn't fire if the condition is already set)
     */
    var isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
            ?: isToolbarCollapseEnabled

    private val activity = fragment.activity

    var toolbar: Toolbar? = null
        private set

    var appbarLayout: AppBarLayout? = null
        private set

    private var isUpNavigationEnabled = true

    private val parentFragment
        get() = fragment.parentFragment

    fun setupAppbar(fragmentView: ViewGroup) {
        appbarLayout = fragment.createAppbarLayout(fragmentView)
        fragmentView.addView(appbarLayout, 0)
        toolbar = appbarLayout?.findViewById(R.id.toolbar)

        val theme = activity?.theme

        if (isLollipop() && theme != null) {
            activity?.window?.apply {
                this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                val color = theme.getResourceIdFromAttrId(R.attr.colorPrimaryDark)
                this.statusBarColor = ContextCompat.getColor(activity, color)
            }
        }

        if (isUpNavigationEnabled && theme != null) {
            logi("Up navigation is enabled. Setting up...")
            toolbar?.navigationIcon = ViewUtility.getNavigationIcon(R.drawable.ic_arrow_back_white_24dp, theme)
            toolbar?.setNavigationContentDescription(R.string.content_description_navigation_icon)
            toolbar?.setNavigationOnClickListener(onNavigationClick)
        }

        invalidateOptionsMenu()
    }

    fun removeAllOptionsMenuExceptSearch() {
        toolbar?.menu?.forEach { it.isVisible = false }
    }

    fun resetOptionsMenu() {
        toolbar?.apply {
            menu.clear()
            onCreateOptionsMenu?.invoke(this)
        }
    }

    fun setupToolbarCollapsing() {
        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing()
        } else {
            disableToolbarCollapsing()
        }
    }

    fun invalidateOptionsMenu() {
        toolbar?.let { toolbar ->
            onCreateOptionsMenu?.invoke(toolbar)
            (activity as? BaseFragment.OnToolbarSetupListener)?.onToolbarSetup(toolbar)
            toolbar.setOnMenuItemClickListener(null)
            toolbar.setOnMenuItemClickListener(onOptionsItemSelected)
        }
    }

    private var onNavigationClick: ((View) -> Unit) = {
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
            // Children don't have toolbars or it's already enabled
            return
        }

        if (appbarLayout?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout) == null) {
            // Collapsing toolbars are custom views
            (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
                it.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            }
        }

        val container = fragment.view?.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is put underneath the toolbar since it is going to be moved out of the
            // way after scrolling
            logd("Setting container layout params to enable scrolling behavior...")
            it.topMargin = 0
            it.behavior = AppBarLayout.ScrollingViewBehavior()
            container.requestLayout()
        }

        container.requestLayout()

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

        val view = fragment.view
        if (view == null) {
            loge("BaseView was null!", IllegalStateException())
            return
        }

        val container = view.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("fragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is underneath the toolbar, so we must add margin so it is below it instead
            val topMarginResource = container.context.theme.getResourceIdFromAttrId(R.attr.actionBarSize)
            it.topMargin = dimen(topMarginResource).toInt()
            it.behavior = null
            container.requestLayout()
        }

        isToolbarCollapseEnabled = false
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
    }

}