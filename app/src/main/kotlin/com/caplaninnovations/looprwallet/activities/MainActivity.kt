package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.view.MenuItem

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.snack
import kotlinx.android.synthetic.main.appbar_main.*

/**
 * As of right now, this will translate into the *Market* screen for traders, when they first
 * open the app (assuming they are signed in & authenticated).
 */
class MainActivity : BottomNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun foo() : Boolean {
        return true
    }

    override val contentView: Int
        get() = R.layout.activity_main

}
