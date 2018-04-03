package org.loopring.looprwallet.walletsignin.fragments

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.signin.EnterPasswordForPhraseFragment
import com.caplaninnovations.looprwallet.fragments.signin.SignInEnterPhraseFragment
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
        val TAG: String = CreateWalletSelectionFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFromKeystoreButton.setOnClickListener {
            pushFragmentTransaction(
                    CreateWalletKeystoreFragment(),
                    CreateWalletKeystoreFragment.TAG
            )
        }

        keystoreHelpButton.setOnClickListener {
            // TODO
        }

        createFromPhraseButton.setOnClickListener {
            pushFragmentTransaction(
                    EnterPasswordForPhraseFragment.createCreationInstance(),
                    EnterPasswordForPhraseFragment.TAG
            )
        }

        phraseHelpButton.setOnClickListener {
            // TODO
        }
    }

}