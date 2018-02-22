package com.caplaninnovations.looprwallet.fragments.createwallet

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_wallet_selection.*

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateWalletSelectionFragment : BaseFragment() {

    override val layoutResource: Int
        get() = R.layout.fragment_create_wallet_selection

    companion object {
        val TAG = CreateWalletSelectionFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFromKeystoreButton.setOnClickListener {
            pushFragmentTransaction(CreateWalletKeystoreFragment(), CreateWalletKeystoreFragment.TAG)
        }

        keystoreHelpButton.setOnClickListener {
            // TODO
        }

        // TODO after Bip39 is included in Web3j library
        createFromPhraseButton.visibility = View.GONE
        createFromPhraseButton.setOnClickListener {
        }

        phraseHelpButton.visibility = View.GONE
        phraseHelpButton.setOnClickListener {
        }
    }

}