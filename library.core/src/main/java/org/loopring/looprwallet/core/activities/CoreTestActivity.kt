package org.loopring.looprwallet.core.activities

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.annotation.VisibleForTesting
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.mapIfNull
import org.loopring.looprwallet.core.fragments.security.ConfirmOldSecurityFragment

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(RestrictTo.Scope.TESTS)
open class CoreTestActivity : BaseActivity(), ConfirmOldSecurityFragment.OnSecurityConfirmedListener {

    var isRunningTest: Boolean? = null

    override val contentViewRes: Int
        get() = R.layout.activity_test_container

    override val activityContainerId: Int
        get() = R.id.activityContainer

    override val isSecureActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isRunningTest()) {
            executeHumanTestCode(savedInstanceState)
        }
    }

    open fun executeHumanTestCode(savedInstanceState: Bundle?) {
        walletClient.createWallet(
                walletName = "loopr-wallet",
                privateKey = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                keystoreContent = null,
                phrase = null
        )
    }

    fun addFragment(fragment: Fragment, tag: String) {
        if (fragment is DialogFragment) {
            fragment.show(supportFragmentManager, tag)
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityContainer, fragment, tag)
                    .commitNow()
        }
    }

    fun removeFragment(fragment: Fragment) {
        if (fragment is DialogFragment) {
            fragment.dismiss()
        } else {
            supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
        }
    }

    @VisibleForTesting
    var isSecurityConfirmed = false
        private set

    override fun onSecurityConfirmed(parameter: Int) {
        isSecurityConfirmed = true
    }

    // MARK - Private Methods

    @Synchronized
    private fun isRunningTest(): Boolean {
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

}