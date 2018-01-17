package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.view.MenuItem
import com.caplaninnovations.looprwallet.R

class WalletActivity : BottomNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
    }

    override val contentView: Int
        get() = R.layout.activity_wallet

    override val navigationItemReselectedListener: (MenuItem) -> Unit
        get() = {}

}
