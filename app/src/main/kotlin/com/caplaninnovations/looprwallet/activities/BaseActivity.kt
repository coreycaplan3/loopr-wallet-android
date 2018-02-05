package com.caplaninnovations.looprwallet.activities

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
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.dagger.LooprProductionComponent
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.utilities.*
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*
import javax.inject.Inject

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

    var isToolbarCollapseEnabled: Boolean = false
        private set

    private companion object {

        private const val KEY_IS_TOOLBAR_COLLAPSED = "_IS_TOOLBAR_COLLAPSED"
        private const val KEY_IS_PROGRESS_DIALOG_SHOWING = "_IS_PROGRESS_DIALOG_SHOWING"
        private const val KEY_PROGRESS_DIALOG_TITLE = "_PROGRESS_DIALOG_TITLE"
    }

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentView: Int

    /**
     * True if this activity requires the user to be authenticated (enter OS passcode) or false
     * otherwise
     */
    abstract val isSecurityActivity: Boolean

    lateinit var looprProductionComponent: LooprProductionComponent

    lateinit var progressDialog: AppCompatDialog

    @Inject
    lateinit var themeSettings: ThemeSettings

    @Inject
    lateinit var securityClient: SecurityClient

    @Inject
    lateinit var realmClient: RealmClient

    @IdRes
    private var progressDialogTitle: Int? = null

    var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        looprProductionComponent = (application as LooprWalletApp).looprProductionComponent
        looprProductionComponent.inject(this)

        this.setTheme(themeSettings.getCurrentTheme())

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
    }

    override fun onResume() {
        super.onResume()

        if (isSecurityActivity && !securityClient.isAndroidKeystoreUnlocked()) {
            this.longToast(R.string.unlock_device)
            securityClient.unlockAndroidKeystore()
        } else if (isSecurityActivity && realm == null) {
            /*
             * We can initialize the Realm now, since the Keystore is unlocked.
             *
             * NOTE, we check if it's null since we don't want to override an already-initialized
             * realm.
             */
            val walletName = securityClient.getCurrentWallet()
            val encryptionKey = securityClient.getCurrentWalletEncryptionKey()
            if (walletName != null && encryptionKey != null) {
                realm = realmClient.getInstance(walletName, encryptionKey)
            } else {
                // There is no current wallet... we need to prompt the user to sign in.
                // This should never happen, since we can't get to a "securityActivity" without
                // passing through the SplashScreen first.
                loge("There is no current wallet... prompting the user to sign in", IllegalStateException())
                securityClient.onNoCurrentWalletSelected(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean = true

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        /*
         * TODO This will be replaced down the road with a "select different wallet and remove
         * TODO wallet" feature
         */
        securityClient.getCurrentWallet()?.let {

            realm.removeListenersAndClose()

            securityClient.removeWallet(it)
            if (securityClient.getCurrentWallet() == null) {
                securityClient.onNoCurrentWalletSelected(this)
            }
        }
        return false
    }

    /**
     * Updates the provided container, based on the current toolbar mode (collapsing or static).
     */
    fun updateContainerBasedOnToolbarMode(container: ViewGroup?) {
        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing(container)
        } else {
            disableToolbarCollapsing(container)
        }
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
        toolbar?.requestLayout()

        (container?.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            it.behavior = null
            container.requestLayout()
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is "under" the actionBar, so we must add margin so it is below it
            it.topMargin = resources.getDimension(getResourceIdFromAttrId(android.R.attr.actionBarSize)).toInt()
            logd("Action bar size: ${it.topMargin}px")
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

        realm.removeListenersAndClose()
    }

}