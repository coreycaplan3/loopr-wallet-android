package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_create_wallet_selection.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.fragments.signin.EnterPasswordForPhraseFragment

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
        val TAG: String = CreateWalletSelectionFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFromKeystoreButton.setOnClickListener {
            pushFragmentTransaction(
                    CreateWalletKeystoreFragment(),
                    CreateWalletKeystoreFragment.TAG,
                    FragmentTransactionController.ANIMATION_HORIZONTAL
            )
        }

        keystoreHelpButton.setOnClickListener {
            // TODO
        }

        createFromPhraseButton.setOnClickListener {
            pushFragmentTransaction(
                    EnterPasswordForPhraseFragment.getCreationInstance(),
                    EnterPasswordForPhraseFragment.TAG,
                    FragmentTransactionController.ANIMATION_HORIZONTAL
            )
        }

        phraseHelpButton.setOnClickListener {
            // TODO
        }
    }

}