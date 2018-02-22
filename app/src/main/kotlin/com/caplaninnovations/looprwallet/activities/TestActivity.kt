package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope.TESTS
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.restorewallet.RestoreWalletKeystoreFragment

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(TESTS)
class TestActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_test_container

    override val isSecurityActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null && intent?.getBooleanExtra("AddFragment", false) == true) {
            addFragment(RestoreWalletKeystoreFragment(), RestoreWalletKeystoreFragment.TAG)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        throw IllegalStateException("Could not perform test because permissions needed to be requested!")
    }

    fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.activityContainer, fragment, tag)
                .commit()

        if (fragment.retainInstance) {
            supportFragmentManager.beginTransaction()
                    .add(fragment, tag)
                    .commit()
        }
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
    }
}