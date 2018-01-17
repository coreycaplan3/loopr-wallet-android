package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.caplaninnovations.looprwallet.R
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 * Created by Corey on 1/17/2018.
 * Project: MeetUp
 * <p></p>
 * Purpose of Class:
 */
abstract class BottomNavigationActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomNavigation.setOnNavigationItemReselectedListener(navigationItemReselectedListener)
    }

    private val navigationItemSelectedListener: (MenuItem) -> Boolean = {
        val intent = when(it.itemId) {
            R.id.bottom_navigation_markets -> Intent(applicationContext, MainActivity::class.java)
            R.id.bottom_navigation_orders -> Intent(applicationContext, OrdersActivity::class.java)
            R.id.bottom_navigation_my_wallet -> Intent(applicationContext, WalletActivity::class.java)
            else -> throw IllegalArgumentException("Invalid id, found: " + it.itemId)
        }

        Log.e("ASD: ", "ID: ${it.itemId}")

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)

        true
    }

    abstract val navigationItemReselectedListener: (MenuItem) -> Unit

}