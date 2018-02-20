package com.caplaninnovations.looprwallet.fragments.createwallet

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.datalayer.WalletCreationPasswordViewModel
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPassword
import com.caplaninnovations.looprwallet.validation.PasswordValidator
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import kotlinx.android.synthetic.main.card_create_wallet_name.*

/**
 * Created by Corey Caplan on 2/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateWalletKeystoreFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateWalletKeystoreFragment::class.simpleName!!
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_wallet_keystore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walletCreationPasswordViewModel = ViewModelProviders.of(activity!!)
                .get(WalletCreationPasswordViewModel::class.java)

        val passwordValidator = PasswordValidator(createWalletNameInputLayout)
        validatorList = listOf(passwordValidator)

        fragmentCreateWalletKeystoreCreateButton.setOnClickListener {
            if (isAllValidatorsValid()) {
//                walletCreationPasswordViewModel.walletCreationPassword.value = WalletCreationPassword()
            } else {

            }
        }
    }

}