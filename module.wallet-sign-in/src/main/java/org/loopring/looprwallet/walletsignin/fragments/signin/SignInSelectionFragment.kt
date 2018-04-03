package org.loopring.looprwallet.walletsignin.fragments.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.walletsignin.fragments.CreateWalletSelectionFragment
import org.loopring.looprwallet.walletsignin.fragments.restorewallet.RestoreWalletSelectionFragment

/**
 * Created by Corey on 2/18/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
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

        restoreWalletButton.setOnLongClickListener {
            // TODO delete me
            val walletName = "debug-loopr-currentWallet"
            val privateKey = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e439"
            (activity as? BaseActivity)?.walletClient?.createWallet(walletName, privateKey)
            startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
            true
        }
    }

}