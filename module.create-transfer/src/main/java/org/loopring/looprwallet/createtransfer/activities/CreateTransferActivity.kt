package org.loopring.looprwallet.createtransfer.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.createtransfer.R
import org.loopring.looprwallet.createtransfer.fragments.SelectTransferContactFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateTransferActivity : BaseActivity() {

    companion object {

        private const val KEY_DEFAULT_ADDRESS = "_DEFAULT_ADDRESS"

        fun route(fragment: Fragment, address: String? = null) {
            val intent = Intent(CoreLooprWalletApp.application, CreateTransferActivity::class.java)
                    .putExtra(KEY_DEFAULT_ADDRESS, address)

            fragment.startActivity(intent)
        }

    }

    override val contentViewRes: Int
        get() = R.layout.activity_create_transfer

    override val isSecureActivity: Boolean
        get() = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO DEFAULT ADDRESS
        if (savedInstanceState == null) {
            pushFragmentTransaction(SelectTransferContactFragment(), SelectTransferContactFragment.TAG)
        }
    }

}