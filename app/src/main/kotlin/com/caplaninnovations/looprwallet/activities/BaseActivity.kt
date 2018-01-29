package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseActivity : AppCompatActivity() {

    var isToolbarCollapseEnabled: Boolean = false

    private val tagIsToolbarCollapsed = "_IsToolbarCollapsed"

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO set theme dynamically (before call to setContentView)
        applicationContext.setTheme(R.style.AppTheme_Light)
        this.setTheme(R.style.AppTheme_Light)

        setContentView(contentView)
        setSupportActionBar(toolbar)

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(tagIsToolbarCollapsed) ?: false

        if(isToolbarCollapseEnabled) {
            enableToolbarCollapsing(null)
        } else {
            disableToolbarCollapsing(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean = true

    /**
     * Enables the toolbar to be collapsed when scrolling
     *
     * @param container The container to which [AppBarLayout.ScrollingViewBehavior] will be applied
     */
    fun enableToolbarCollapsing(container: ViewGroup?) {
        val layoutParams = (toolbar?.layoutParams as? AppBarLayout.LayoutParams)
        layoutParams?.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS

        (container?.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            it.behavior = AppBarLayout.ScrollingViewBehavior()
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is put "under" the actionBar since it is going to be moved out of the
            // way after scrolling
            it.topMargin = 0
            activityContainer.layoutParams = it
        }

        isToolbarCollapseEnabled = true
    }

    /**
     * Disables the toolbar from being collapsed when scrolling
     *
     * @param container The container to which [AppBarLayout.ScrollingViewBehavior] will be
     * **removed**
     */
    fun disableToolbarCollapsing(container: ViewGroup?) {
        val layoutParams = (toolbar?.layoutParams as? AppBarLayout.LayoutParams)
        layoutParams?.scrollFlags = 0

        (container?.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            it.behavior = null
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is "under" the actionBar, so we must add margin so it is below it
            it.topMargin = resources.getDimension(getResourceIdFromAttrId(android.R.attr.actionBarSize)).toInt()
            activityContainer.layoutParams = it
        }

        isToolbarCollapseEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(tagIsToolbarCollapsed, isToolbarCollapseEnabled)
    }

}