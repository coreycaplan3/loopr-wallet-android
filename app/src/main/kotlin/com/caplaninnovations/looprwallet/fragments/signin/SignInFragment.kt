package com.caplaninnovations.looprwallet.fragments.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.createwallet.CreateWalletSelectionFragment
import com.caplaninnovations.looprwallet.models.android.fragments.FragmentStackTransactionController
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * Created by Corey on 2/18/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class SignInFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_sign_in

    companion object {
        val TAG: String = SignInFragment::class.simpleName!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNewWalletButton.setOnClickListener {
            executeFragmentTransaction(CreateWalletSelectionFragment(), CreateWalletSelectionFragment.TAG)
        }

        restoreWalletButton.setOnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
        }
    }

}