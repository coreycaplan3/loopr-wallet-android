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
import android.view.WindowManager.LayoutParams.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.dagger.LooprProductionComponent
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.utilities.*
import io.realm.Realm
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

    companion object {

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

    private lateinit var looprProductionComponent: LooprProductionComponent

    lateinit var progressDialog: AppCompatDialog

    @Inject
    lateinit var themeSettings: ThemeSettings

    @Inject
    lateinit var securityClient: SecurityClient

    @Inject
    lateinit var realmClient: RealmClient

    lateinit var activityContainer: ViewGroup

    @IdRes
    var progressDialogTitle: Int? = null
        private set

    var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        looprProductionComponent = (application as LooprWalletApp).looprProductionComponent
        looprProductionComponent.inject(this)

        this.setTheme(themeSettings.getCurrentTheme())

        setContentView(contentView)

        window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)

        activityContainer = findViewById(R.id.activityContainer)

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
        setSupportActionBar(toolbar)
        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED) ?: false

        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing()
        } else {
            disableToolbarCollapsing()
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

            RealmUtility.removeListenersAndClose(realm)

            securityClient.removeWallet(it)
            if (securityClient.getCurrentWallet() == null) {
                securityClient.onNoCurrentWalletSelected(this)
            }
        }
        return false
    }

    /**
     * Enables the toolbar to be collapsed when scrolling
     */
    fun enableToolbarCollapsing() {
        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is put underneath the toolbar since it is going to be moved out of the
            // way after scrolling
            it.topMargin = 0
            it.behavior = AppBarLayout.ScrollingViewBehavior()
            activityContainer.layoutParams = it
            activityContainer.requestLayout()
        }

        isToolbarCollapseEnabled = true
    }

    /**
     * Disables the toolbar from being collapsed when scrolling
     */
    fun disableToolbarCollapsing() {
        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = 0
        }

        (activityContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is underneath the toolbar, so we must add margin so it is below it instead
            it.topMargin = resources.getDimension(getResourceIdFromAttrId(android.R.attr.actionBarSize)).toInt()
            it.behavior = null
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

        RealmUtility.removeListenersAndClose(realm)
    }

}