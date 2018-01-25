package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Created by Corey on 1/14/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseActivity : AppCompatActivity() {

    private var isToolbarCollapseEnabled: Boolean = false

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean = true

    /**
     * Enables the toolbar to be collapsed when scrolling
     *
     * @param view The view to which *scrollingViewBehavior* will be applied
     */
    fun enableToolbarCollapsing(view: ViewGroup) {
        val layoutParams = (toolbar?.layoutParams as? AppBarLayout.LayoutParams)
        layoutParams?.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS

        isToolbarCollapseEnabled = true
    }

    /**
     * Enables the toolbar to be collapsed when scrolling
     */
    fun disableToolbarCollapsing() {
        val layoutParams = (toolbar?.layoutParams as? AppBarLayout.LayoutParams)
        layoutParams?.scrollFlags = 0

        isToolbarCollapseEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(tagIsToolbarCollapsed, isToolbarCollapseEnabled)
    }

}