package org.loopring.looprwallet.walletsignin.fragments.signin

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.walletsignin.fragments.createwallet.CreateWalletSelectionFragment
import org.loopring.looprwallet.walletsignin.fragments.restorewallet.RestoreWalletSelectionFragment

/**
 * Created by Corey on 2/18/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SignInSelectionFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_sign_in

    companion object {
        val TAG: String = SignInSelectionFragment::class.simpleName!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNewWalletButton.setOnClickListener {
            pushFragmentTransaction(CreateWalletSelectionFragment(), CreateWalletSelectionFragment.TAG)
        }

        restoreWalletButton.setOnClickListener {
            pushFragmentTransaction(RestoreWalletSelectionFragment(), RestoreWalletSelectionFragment.TAG)
        }
    }

}