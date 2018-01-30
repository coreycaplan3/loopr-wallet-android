package com.caplaninnovations.looprwallet.activities

import android.os.Bundle

import com.caplaninnovations.looprwallet.R

/**
 * As of right now, this will translate into the *Market* screen for traders, when they first
 * open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BottomNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override val contentView: Int
        get() = R.layout.activity_main

}
