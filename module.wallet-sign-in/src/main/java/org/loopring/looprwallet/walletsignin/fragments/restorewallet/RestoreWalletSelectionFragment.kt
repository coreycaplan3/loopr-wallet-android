package org.loopring.looprwallet.walletsignin.fragments.restorewallet

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_restore_wallet_selection.*
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.walletsignin.fragments.signin.EnterPasswordForPhraseFragment

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class RestoreWalletSelectionFragment : BaseFragment() {

    companion object {
        val TAG: String = RestoreWalletSelectionFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_wallet_selection


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreWalletKeystoreButton.setOnClickListener {
            pushFragmentTransaction(RestoreWalletKeystoreFragment(), RestoreWalletKeystoreFragment.TAG)
        }

        restoreWalletPhraseButton.setOnClickListener {
            pushFragmentTransaction(
                    EnterPasswordForPhraseFragment.createRestorationInstance(),
                    EnterPasswordForPhraseFragment.TAG
            )
        }

        restoreWalletPrivateKeyButton.setOnClickListener {
            pushFragmentTransaction(RestoreWalletPrivateKeyFragment(), RestoreWalletPrivateKeyFragment.TAG)
        }

    }

}