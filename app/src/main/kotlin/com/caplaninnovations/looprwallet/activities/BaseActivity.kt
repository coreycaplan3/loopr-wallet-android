package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.LooprThemeSettings
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import com.caplaninnovations.looprwallet.utilities.RealmUtility
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import com.caplaninnovations.looprwallet.utilities.logd
import com.caplaninnovations.looprwallet.utilities.longToast
import io.realm.Realm
import io.realm.android.CipherClient
import io.realm.android.internal.android.crypto.SyncCryptoFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 * To run any necessary initialization code before starting a standardized
 * activity. A standardized activity is an activity that is core to the application and is not one
 * of the following:
 * - [SignInActivity]
 * - [IntroActivity]
 * - [WelcomeActivity]
 */
abstract class BaseActivity : AppCompatActivity() {

    private var isToolbarCollapseEnabled: Boolean = false

    private companion object {

        private const val KEY_IS_TOOLBAR_COLLAPSED = "_IS_TOOLBAR_COLLAPSED"
        private const val KEY_IS_PROGRESS_DIALOG_SHOWING = "_IS_PROGRESS_DIALOG_SHOWING"
        private const val KEY_PROGRESS_DIALOG_TITLE = "_PROGRESS_DIALOG_TITLE"
    }

    private lateinit var cipherClient: CipherClient

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

    /**
     * True if this activity requires the user to be authenticated (enter OS passcode) or false
     * otherwise
     */
    abstract val isSecurityActivity: Boolean

    lateinit var progressDialog: AppCompatDialog

    @IdRes
    var progressDialogTitle: Int? = null

    var realm: Realm? = null

    var currentWallet: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTheme(LooprThemeSettings(this).getCurrentTheme())

        setContentView(contentView)
        setSupportActionBar(toolbar)

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED) ?: false

        /*
         * Progress Dialog Setup
         */
        progressDialog = object : AppCompatDialog(this) {
            override fun setTitle(titleId: Int) {
                super.setTitle(titleId)
                progressDialogTitle = titleId
            }
        }

        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        if (savedInstanceState?.getBoolean(KEY_IS_PROGRESS_DIALOG_SHOWING) == true) {
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

        if (isSecurityActivity) {
            logd("Setting up security activity...")
            setupCipherClient()
        } else {
            logd("Not a security activity...")
        }
    }

    override fun onResume() {
        super.onResume()

        if (isSecurityActivity && !cipherClient.isKeystoreUnlocked) {
            this.longToast(R.string.unlock_device)
            cipherClient.unlockKeystore()
        } else if (isSecurityActivity) {
            setupCipherClient()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean = true

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        currentWallet?.let {
            LooprWalletSettings(this).removeWallet(it)
            startActivity(Intent(this, SignInActivity::class.java))
        }
        return false
    }

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
        toolbar.requestLayout()

        (container?.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            it.behavior = null

            container.requestLayout()
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is "under" the actionBar, so we must add margin so it is below it
            it.topMargin = resources.getDimension(getResourceIdFromAttrId(android.R.attr.actionBarSize)).toInt()
            activityContainer.layoutParams = it
            activityContainer.requestLayout()
        }

        isToolbarCollapseEnabled = false
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
        outState?.putBoolean(KEY_IS_PROGRESS_DIALOG_SHOWING, progressDialog.isShowing)
        progressDialogTitle?.let { outState?.putInt(KEY_PROGRESS_DIALOG_TITLE, it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm?.removeAllChangeListeners()
        realm?.close()
    }

    // MARK - Private Methods

    private fun setupCipherClient() {
        cipherClient = CipherClient(this)

        if (!cipherClient.isKeystoreUnlocked) {
            cipherClient.unlockKeystore()
        } else {
            realm = RealmUtility.initialize(this)
        }
    }
}