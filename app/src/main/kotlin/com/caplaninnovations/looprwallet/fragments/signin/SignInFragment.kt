package com.caplaninnovations.looprwallet.fragments.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.createwallet.CreateWalletSelectionFragment
import com.caplaninnovations.looprwallet.fragments.restorewallet.RestoreWalletSelectionFragment
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
            pushFragmentTransaction(CreateWalletSelectionFragment(), CreateWalletSelectionFragment.TAG)
        }

        restoreWalletButton.setOnClickListener {
            pushFragmentTransaction(RestoreWalletSelectionFragment(), RestoreWalletSelectionFragment.TAG)
        }

        restoreWalletButton.setOnLongClickListener {
            // TODO delete me
            val walletName = "corey-wallet"
            val privateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e439"
            (activity as? BaseActivity)?.securityClient?.createWallet(walletName, privateKey)
            startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
            true
        }
    }

}