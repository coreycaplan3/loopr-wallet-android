package org.loopring.looprwallet.home.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.extensions.ifNotNull
import org.loopring.looprwallet.core.extensions.logi
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment.OnSecurityConfirmedListener
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.loopring.looprwallet.core.models.android.fragments.LooprFragmentPagerAdapter
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.presenters.BottomNavigationPresenter
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.home.R
import org.loopring.looprwallet.homemarkets.fragments.HomeMarketsParentFragment
import org.loopring.looprwallet.homemywallet.fragments.MyWalletFragment
import org.loopring.looprwallet.homeorders.fragments.HomeOrdersParentFragment
import org.loopring.looprwallet.hometransfers.fragments.ViewTransfersFragment
import org.loopring.looprwallet.walletsignin.activities.SignInActivity

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class: As of right now, this will translate into the *Market* screen for traders,
 * when they first open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity(), OnSecurityConfirmedListener {

    companion object {

        /**
         * Starts this activity (as normal), clearing any previous tasks which may have pointed to
         * here
         */
        fun routeAndClearOldTasks(activity: Activity) {
            val intent = Intent(CoreLooprWalletApp.context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            activity.startActivity(intent)
        }

        /**
         * @return An intent used to start this activity (as normal), clearing any previous tasks
         * which may have pointed to here
         */
        fun routeClearOldTasksAndRemoveCurrentWallet(activity: Activity) {
            val intent = Intent(CoreLooprWalletApp.context, MainActivity::class.java)
                    .putExtra(KEY_REMOVE_CURRENT_WALLET, true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            activity.startActivity(intent)
        }

        private const val KEY_REMOVE_CURRENT_WALLET = "_REMOVE_CURRENT_WALLET"
    }

    override val contentViewRes: Int
        get() = R.layout.activity_main

    override val isSecureActivity: Boolean
        get() = true

    private val pagerAdapter by lazy {
        LooprFragmentPagerAdapter(supportFragmentManager, listOf(
                str(R.string.markets) to HomeMarketsParentFragment(),
                str(R.string.orders) to HomeOrdersParentFragment(),
                str(R.string.transfers) to ViewTransfersFragment(),
                str(R.string.my_wallet) to MyWalletFragment()
        ))
    }

    lateinit var bottomNavigationFragmentStackHistory: BottomNavigationFragmentStackHistory

    lateinit var bottomNavigationPresenter: BottomNavigationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isRemovingCurrentWallet = intent.getBooleanExtra(KEY_REMOVE_CURRENT_WALLET, false)
        if (isRemovingCurrentWallet && savedInstanceState == null) {
            walletClient.getCurrentWallet()?.walletName?.let { walletClient.removeWallet(it) }

            if (walletClient.getAllWallets().isEmpty()) {
                // We need to go back to the SignInActivity
                SignInActivity.route(this, false)
            }
        }

        bottomNavigationFragmentStackHistory = BottomNavigationFragmentStackHistory(false, savedInstanceState)

        bottomNavigationView.inflateMenu(R.menu.home_bottom_navigation_menu)

        bottomNavigationPresenter = BottomNavigationPresenter(
                activity = this,
                bottomNavigationView = bottomNavigationView,
                viewPager = mainViewPager,
                pagerAdapter = pagerAdapter,
                bottomNavigationFragmentStackHistory = bottomNavigationFragmentStackHistory,
                savedInstanceState = savedInstanceState
        )
    }

    override fun onSecurityConfirmed(parameter: Int) {
        val fragment = pagerAdapter.currentFragment
        if (fragment is OnSecurityConfirmedListener) {
            fragment.onSecurityConfirmed(parameter)
        }
    }

    override fun onBackPressed() {
        if (bottomNavigationPresenter.onBackPressed()) {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        bottomNavigationPresenter.onSaveInstanceState(outState)
    }

    // MARK - Private Methods

    /**
     * If this variable is **NOT** null when the drawer is closed, we can invoke it :)
     */
    private var onItemSelected: (() -> Unit)? = null

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)

        supportActionBar?.apply {
            logi("Setting up the navigation drawer...")
            toolbar?.let { setupNavigationDrawer(it) }
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            homeNavigationDrawerLayout.openDrawer(Gravity.START)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupNavigationDrawer(toolbar: Toolbar) {
        setupNavigationHeaderView()
        setupNavigationDrawerMenu(toolbar)
    }

    private fun setupNavigationHeaderView() {
        val headerView = homeNavigationView.getHeaderView(0)

        val addressLabel = headerView.findViewById<TextView>(R.id.navigationHeaderWalletAddressLabel)
        val walletNameLabel = headerView.findViewById<TextView>(R.id.navigationHeaderWalletNameLabel)

        val currentWallet = walletClient.getCurrentWallet()
        when (currentWallet) {
            null -> {
                addressLabel.visibility = View.GONE
                walletNameLabel.setText(R.string.watch_only)
            }
            else -> {
                addressLabel.visibility = View.VISIBLE

                walletNameLabel.text = currentWallet.walletName
                addressLabel.text = currentWallet.credentials.address
            }
        }
    }

    private fun setupNavigationDrawerMenu(toolbar: Toolbar) {
        // Clear the old menu
        homeNavigationView.menu.clear()
        homeNavigationView.inflateMenu(R.menu.home_navigation_drawer_menu)

        // Setup the current selected menu item
        val currentWalletName = walletClient.getCurrentWallet()?.walletName
        val allWallets = walletClient.getAllWallets()
        allWallets.forEachIndexed { index, item ->
            homeNavigationView.menu.add(R.id.menuWalletNameGroup, Menu.NONE, index, item.walletName).let {
                if (item.walletName == currentWalletName) {
                    it.isChecked = true
                }
            }
        }

        homeNavigationView.menu.findItem(R.id.menuAddNewWallet).apply {
            this.icon?.let { DrawableCompat.setTint(it, Color.BLACK) }
        }

        homeNavigationView.menu.findItem(R.id.menuDeleteWallet).apply {
            this.icon?.let { DrawableCompat.setTint(it, Color.BLACK) }
        }

        setupNavigationItemClickListener(toolbar, allWallets)
    }

    private fun setupNavigationItemClickListener(toolbar: Toolbar, allWallets: List<LooprWallet>) {
        val listener = object : ActionBarDrawerToggle(
                this,
                homeNavigationDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)

                onItemSelected?.invoke()
                onItemSelected = null
            }
        }

        @Suppress("DEPRECATION")
        homeNavigationDrawerLayout.setDrawerListener(listener)

        homeNavigationView.setNavigationItemSelectedListener { menuItem ->
            when {
                menuItem.itemId == R.id.menuAddNewWallet -> {
                    onItemSelected = { SignInActivity.route(this, true) }
                }
                menuItem.itemId == R.id.menuDeleteWallet -> {
                    onItemSelected = { showDeleteWalletDialogAdapter(toolbar, allWallets) }
                }
                else -> allWallets.find { it.walletName == menuItem.title }?.ifNotNull {
                    // We found the wallet that matches the title's name
                    val currentWallet = walletClient.getCurrentWallet()
                    if (currentWallet != null && currentWallet != it) {
                        // We just selected a different wallet from the current one
                        onItemSelected = {
                            walletClient.selectNewCurrentWallet(it.walletName)
                            MainActivity.routeAndClearOldTasks(this@MainActivity)
                        }
                    }
                }
            }

            homeNavigationDrawerLayout.closeDrawers()

            return@setNavigationItemSelectedListener true
        }
    }

    private fun showDeleteWalletDialogAdapter(toolbar: Toolbar, allWallets: List<LooprWallet>) {
        val adapterItems = allWallets.map { it.walletName }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterItems)

        AlertDialog.Builder(this)
                .setAdapter(adapter) { dialog, position ->
                    if (walletClient.getCurrentWallet() == allWallets[position]) {
                        // We need to restart the activity and remove the wallet while there are NO
                        // realms open
                        onItemSelected = { MainActivity.routeClearOldTasksAndRemoveCurrentWallet(this) }
                        dialog.dismiss()
                        homeNavigationDrawerLayout.closeDrawers()
                    } else {
                        // The wallet to delete is not the current one
                        // "It's not my wallet" - Patrick
                        walletClient.removeWallet(allWallets[position].walletName)

                        // Reset the menu
                        setupNavigationDrawerMenu(toolbar)
                        dialog.dismiss()
                    }
                }.show()
    }

}
