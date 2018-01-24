package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.caplaninnovations.looprwallet.R

class MyWalletActivity : BottomNavigationActivity() {

    override val selectedTab: String
        get() = tagMyWallet

    override val navigationContainer: Int
        get() = R.layout.activity_my_wallet

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // TODO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}
