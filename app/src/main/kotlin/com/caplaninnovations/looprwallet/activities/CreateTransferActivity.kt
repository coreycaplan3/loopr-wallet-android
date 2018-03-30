package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.transfers.SelectContactFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateTransferActivity: BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_create_transfer

    override val isSecureActivity: Boolean
        get() = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(SelectContactFragment(), SelectContactFragment.TAG)
        }
    }

}