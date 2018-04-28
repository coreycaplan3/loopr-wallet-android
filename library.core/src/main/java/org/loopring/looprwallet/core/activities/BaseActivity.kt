@file:Suppress("DEPRECATION")

package org.loopring.looprwallet.core.activities

import android.app.ProgressDialog
import android.graphics.Rect
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.delegates.PermissionDelegate
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.models.settings.ThemeSettings
import org.loopring.looprwallet.core.presenters.SearchViewPresenter.OnSearchViewChangeListener
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.wallet.WalletClient
import javax.inject.Inject

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To run any necessary initialization code before starting a standardized
 * activity.
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {

        private const val KEY_IS_PROGRESS_DIALOG_SHOWING = "_IS_PROGRESS_DIALOG_SHOWING"
        private const val KEY_PROGRESS_DIALOG_MESSAGE = "_PROGRESS_DIALOG_MESSAGE"

        private const val KEY_IS_KEYBOARD_HIDDEN = "_IS_KEYBOARD_HIDDEN"
    }

    /**
     * A layout-resource used to set the *contentView* of the current activity
     */
    abstract val contentViewRes: Int

    /**
     * True if this activity requires the user to be authenticated (enter OS passcode) or false
     * otherwise
     */
    abstract val isSecureActivity: Boolean

    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var themeSettings: ThemeSettings

    @Inject
    lateinit var walletClient: WalletClient

    private var isKeyboardHidden = true

    private var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    var progressDialogMessage: String? = null
        private set

    private lateinit var permissionDelegates: List<PermissionDelegate>

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }

        // Setup the Dagger injection component
        coreLooprComponent.inject(this)

        // Setup the theme
        this.setTheme(themeSettings.getCurrentTheme())

        // Setup the view hierarchy
        setContentView(contentViewRes)

        // Setup the permission handlers
        permissionDelegates = getAllPermissionHandlers()
        permissionDelegates.filter { it.shouldRequestPermissionNow }.forEach { it.requestPermission() }

        // Setup keyboard related stuff
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        isKeyboardHidden = savedInstanceState?.getBoolean(KEY_IS_KEYBOARD_HIDDEN, true) ?: true
        setupKeyboardLayoutListener()

        // Setup the progress dialog
        setupProgressDialog(savedInstanceState)
    }

    private fun setupKeyboardLayoutListener() {
        keyboardLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {

            private val rect = Rect()
            private val visibleThreshold = dimen(R.dimen.keyboard_threshold)
            private var wasOpened = false

            override fun onGlobalLayout() {
                val root = getRootLayout()
                root.getWindowVisibleDisplayFrame(rect)
                val heightDiff = root.rootView.height - rect.height()
                val isOpen = heightDiff > visibleThreshold
                if (isOpen == wasOpened) {
                    // keyboard state has not changed
                    return
                }

                wasOpened = isOpen

                if (isOpen) {
                    onShowKeyboard()
                } else {
                    onHideKeyboard()
                }
            }
        }

        keyboardLayoutListener?.let { getRootLayout().viewTreeObserver.addOnGlobalLayoutListener(it) }
    }

    override fun onResume() {
        super.onResume()

        // Checks that we have a currentWallet, if necessary and takes appropriate action if there
        // a "state" problem. Keep in mind, onResume could be called after the user WAS signed in
        // but decided to clear all of their data. With this in mind, the user can be signed out
        // at ANY moment
        checkWalletStatus()
    }

    fun checkWalletStatus() {
        if (!walletClient.isAndroidKeystoreUnlocked()) {
            this.longToast(R.string.unlock_device)
            walletClient.unlockAndroidKeystore()
        } else if (isSecureActivity && walletClient.getCurrentWallet() == null) {
            // There is no current wallet... we need to prompt the user to sign in.
            // This should never happen, since we can't get to a "securityActivity" without
            // passing through the SplashScreen first.
            loge("There is no current currentWallet... prompting the user to sign in", IllegalStateException())
            walletClient.noCurrentWalletSelected(this)
        }
    }

    open fun getAllPermissionHandlers(): List<PermissionDelegate> {
        // Default is to return an empty list
        return listOf()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionDelegates.forEach {
            it.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.activityContainer)
        if (currentFragment is OnSearchViewChangeListener && currentFragment.searchViewPresenter.isExpanded) {
            // The searchView is expanded. Let's collapse it
            currentFragment.searchViewPresenter.collapseSearchView()
            return
        }

        popFragmentTransaction()
    }

    open fun onHideKeyboard() {
        val fragment = (supportFragmentManager.findFragmentById(R.id.activityContainer) as? BaseFragment)
        fragment?.onHideKeyboard()
    }

    open fun onShowKeyboard() {
        val fragment = (supportFragmentManager.findFragmentById(R.id.activityContainer) as? BaseFragment)
        fragment?.onShowKeyboard()
    }

    /**
     * Pushes the given fragment onto the stack and saves the old one
     */
    open fun pushFragmentTransaction(fragment: BaseFragment, fragmentTag: String) {
        FragmentTransactionController(R.id.activityContainer, fragment, fragmentTag)
                .apply {
                    transition = FragmentTransaction.TRANSIT_FRAGMENT_OPEN
                }
                .commitTransaction(supportFragmentManager)
    }

    open fun popFragmentTransaction() {
        if (!supportFragmentManager.isStateSaved) {
            // We don't want to change the state after the fragment manager already saved its state.
            // This would result in an IllegalStateException being thrown otherwise.
            supportFragmentManager.popBackStackImmediate()
            if (supportFragmentManager.backStackEntryCount == 0) {
                // There are no fragments left in the stack
                finish()
                return
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(KEY_IS_KEYBOARD_HIDDEN, isKeyboardHidden)
        outState?.putBoolean(KEY_IS_PROGRESS_DIALOG_SHOWING, progressDialog.isShowing)
        outState?.putString(KEY_PROGRESS_DIALOG_MESSAGE, progressDialogMessage)

        progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        val rootLayout = findViewById<ViewGroup>(R.id.activityContainer)
        keyboardLayoutListener?.let { rootLayout.viewTreeObserver.removeGlobalOnLayoutListener(it) }
    }

    // MARK - Private Methods

    private fun setupProgressDialog(savedInstanceState: Bundle?) {
        progressDialog = object : ProgressDialog(this) {
            override fun setMessage(message: CharSequence?) {
                super.setMessage(message)
                progressDialogMessage = message?.toString()
            }
        }

        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        if (savedInstanceState?.getBoolean(KEY_IS_PROGRESS_DIALOG_SHOWING) == true) {
            progressDialogMessage = savedInstanceState.getString(KEY_PROGRESS_DIALOG_MESSAGE)
            progressDialog.show()
        }
    }

    private fun getRootLayout(): View {
        return findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
    }

}