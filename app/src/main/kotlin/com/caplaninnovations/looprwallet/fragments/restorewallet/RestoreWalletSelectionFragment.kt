package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.signin.WalletEnterPhraseFragment
import kotlinx.android.synthetic.main.fragment_restore_wallet_selection.*

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

        // TODO
        restoreWalletPhraseButton.visibility = View.GONE
        restoreWalletPhraseButton.setOnClickListener {
            pushFragmentTransaction(WalletEnterPhraseFragment(), WalletEnterPhraseFragment.TAG)
        }

        restoreWalletPrivateKeyButton.setOnClickListener {
            pushFragmentTransaction(RestoreWalletPrivateKeyFragment(), RestoreWalletPrivateKeyFragment.TAG)
        }

    }

}