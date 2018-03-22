package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope.TESTS
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.mapIfNull
import com.caplaninnovations.looprwallet.fragments.transfers.CreateTransferAmountFragment
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationKeystore

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(TESTS)
class TestActivity : BaseActivity() {

    var isRunningTest: Boolean? = null

    @Synchronized
    fun isRunningTest(): Boolean {
        return isRunningTest.mapIfNull {
            try {
                Class.forName("android.support.test.espresso.Espresso")
                isRunningTest = true
                true
            } catch (e: ClassNotFoundException) {
                isRunningTest = false
                false
            }
        }
    }

    override val contentView: Int
        get() = R.layout.activity_test_container

    override val isSecureActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null && !isRunningTest()) {
            // used for testing a sole fragment
            val wallet = WalletCreationKeystore("loopr-currentWallet", "looprwallet")
            walletClient.createWallet("loopr-wallet", "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
            addFragment(
                    CreateTransferAmountFragment.createInstance("0xabcdef1234567890abcdef123456789012345678"),
                    CreateTransferAmountFragment.TAG
            )
        }
    }

    fun addFragment(fragment: Fragment, tag: String) {
        if(fragment is DialogFragment) {
            fragment.show(supportFragmentManager, tag)
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityContainer, fragment, tag)
                    .commitNow()
        }
    }

    fun removeFragment(fragment: Fragment) {
        if(fragment is DialogFragment) {
            fragment.dismiss()
        } else {
            supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
        }
    }
}