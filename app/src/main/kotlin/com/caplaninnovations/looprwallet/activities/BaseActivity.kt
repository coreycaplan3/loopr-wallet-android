package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.Menu
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.LooprThemeSettings
import com.caplaninnovations.looprwallet.realm.LooprRealm
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
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
    private val tagIsProgressDialogShowing = "_IsProgressDialogShowing"

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

    lateinit var progressDialog: AppCompatDialog

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTheme(LooprThemeSettings(this).getCurrentTheme())

        setContentView(contentView)
        setSupportActionBar(toolbar)

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(tagIsToolbarCollapsed) ?: false

        /*
         * Progress Dialog Setup
         */
        progressDialog = AppCompatDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        if (savedInstanceState?.getBoolean(tagIsProgressDialogShowing) == true) {
            progressDialog.show()
        }

        /*
         * Toolbar Setup
         */
        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing(null)
        } else {
            disableToolbarCollapsing(null)
        }

        /*
         * Realm setup
         */
        // TODO find out currently signed-in wallet
        realm = LooprRealm.get("realm-name", ByteArray(1))
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
        outState?.putBoolean(tagIsProgressDialogShowing, progressDialog.isShowing)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.removeAllChangeListeners()
        realm.close()
    }

}