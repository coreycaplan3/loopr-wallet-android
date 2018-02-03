package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.MenuItem

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.navigation.BottomNavigationHandler
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * As of right now, this will translate into the *Market* screen for traders, when they first
 * open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BaseActivity(), BottomNavigationHandler.OnTabVisibilityChangeListener {

    override val contentView: Int
        get() = R.layout.activity_main

    override val isSecurityActivity: Boolean
        get() = true

    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigationHandler = BottomNavigationHandler(this, savedInstanceState)
    }

    override fun onBackPressed() {
        if (bottomNavigationHandler.onBackPressed()) {
            finish()
        }
    }

    override fun onShowTabLayout(tabLayout: TabLayout?) {
        bottomNavigationHandler.onShowTabLayout(tabLayout)
    }

    override fun onHideTabLayout(tabLayout: TabLayout?) {
        bottomNavigationHandler.onHideTabLayout(tabLayout)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        bottomNavigationHandler.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        bottomNavigation.clearOnTabSelectedListeners()
    }

}
