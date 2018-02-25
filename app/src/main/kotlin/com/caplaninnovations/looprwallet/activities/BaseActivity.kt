package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.WindowManager.LayoutParams.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.dagger.LooprProductionComponent
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.PermissionHandler
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackHistory
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import com.caplaninnovations.looprwallet.models.security.SecurityClient
import com.caplaninnovations.looprwallet.realm.RealmClient
import com.caplaninnovations.looprwallet.utilities.*
import io.realm.Realm
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

    companion object {

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

    /**
     * A stack history used for creating a backstack for fragments. To start using it, call
     * [pushFragmentTransaction] in the activity's [onCreate] when the activity is created for
     * the first time.
     */
    open lateinit var fragmentStackHistory: FragmentStackHistory

    @IdRes
    var progressDialogTitle: Int? = null
        private set

    var realm: Realm? = null

    private lateinit var permissionHandlers: List<PermissionHandler>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        looprProductionComponent = (application as LooprWalletApp).looprProductionComponent
        looprProductionComponent.inject(this)

        this.setTheme(themeSettings.getCurrentTheme())

        setContentView(contentView)

        window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)

        fragmentStackHistory = FragmentStackHistory(true, savedInstanceState)

        permissionHandlers = getAllPermissionHandlers()
        permissionHandlers.filter { it.shouldRequestPermissionNow }
                .forEach { it.requestPermission() }

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
            val wallet = securityClient.getCurrentWallet()
            if (wallet != null) {
                realm = realmClient.getInstance(wallet.walletName, wallet.realmKey)
            } else {
                // There is no current wallet... we need to prompt the user to sign in.
                // This should never happen, since we can't get to a "securityActivity" without
                // passing through the SplashScreen first.
                loge("There is no current wallet... prompting the user to sign in", IllegalStateException())
                securityClient.onNoCurrentWalletSelected(this)
            }
        }
    }

    fun removeWalletCurrentWallet() {
        securityClient.getCurrentWallet()?.let {

            RealmUtility.removeListenersAndClose(realm)

            securityClient.removeWallet(it.walletName)
            if (securityClient.getCurrentWallet() == null) {
                securityClient.onNoCurrentWalletSelected(this)
            }
        }
    }

    open fun getAllPermissionHandlers(): List<PermissionHandler> {
        // Default is to return an empty list
        return listOf()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionHandlers.forEach {
            it.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        popFragmentTransaction()
    }

    /**
     * Pushes the given fragment onto the stack and saves the old one
     */
    open fun pushFragmentTransaction(fragment: BaseFragment, fragmentTag: String) {
        val oldFragment = supportFragmentManager.findFragmentById(R.id.activityContainer)
        FragmentStackTransactionController(R.id.activityContainer, fragment, fragmentTag)
                .commitTransactionNow(supportFragmentManager, oldFragment)

        fragmentStackHistory.push(fragmentTag)
    }

    open fun popFragmentTransaction() {
        val oldFragment = supportFragmentManager.findFragmentByTag(fragmentStackHistory.pop())

        val newFragmentTag = fragmentStackHistory.peek()
        if (newFragmentTag == null) {
            // There are no fragments left in the stack
            finish()
            return
        }

        val newFragment = supportFragmentManager.findFragmentByTag(newFragmentTag)

        newFragment?.let {
            FragmentStackTransactionController(R.id.activityContainer, newFragment, newFragmentTag)
                    .commitTransactionNow(supportFragmentManager, oldFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        fragmentStackHistory.saveState(outState)

        outState?.putBoolean(KEY_IS_PROGRESS_DIALOG_SHOWING, progressDialog.isShowing)
        progressDialogTitle?.let { outState?.putInt(KEY_PROGRESS_DIALOG_TITLE, it) }

        progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        RealmUtility.removeListenersAndClose(realm)
    }

}