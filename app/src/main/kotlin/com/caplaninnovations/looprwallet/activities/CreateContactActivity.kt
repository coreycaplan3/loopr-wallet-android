package com.caplaninnovations.looprwallet.activities

import android.content.Intent
import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.contacts.CreateContactFragment

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateContactActivity : BaseActivity() {

    companion object {

        private const val KEY_ADDRESS = "_ADDRESS"

        fun createIntent(address: String?): Intent {
            return Intent(LooprWalletApp.context, CreateContactActivity::class.java)
                    .putExtra(KEY_ADDRESS, address)
        }

    }

    override val contentView: Int
        get() = R.layout.activity_create_contact

    override val isSecureActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val address = intent.getStringExtra(KEY_ADDRESS)

            pushFragmentTransaction(
                    CreateContactFragment.createInstance(address),
                    CreateContactFragment.TAG
            )
        }

    }

}