package com.caplaninnovations.looprwallet.activities

import android.os.Bundle
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.signin.SignInFragment

/**
 * Created by Corey on 1/14/2018
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
class SignInActivity : BaseActivity() {

    override val contentView: Int
        get() = R.layout.activity_sign_in

    override val isSecurityActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            pushFragmentTransaction(SignInFragment(), SignInFragment.TAG)
        }
    }

}
