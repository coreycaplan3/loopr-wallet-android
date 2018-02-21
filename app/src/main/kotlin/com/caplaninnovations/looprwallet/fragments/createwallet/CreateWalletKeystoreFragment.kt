package com.caplaninnovations.looprwallet.fragments.createwallet

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.datalayer.WalletCreationPasswordViewModel
import com.caplaninnovations.looprwallet.dialogs.ConfirmPasswordDialog
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPassword
import com.caplaninnovations.looprwallet.utilities.allNonnull
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.validation.PasswordValidator
import com.caplaninnovations.looprwallet.validation.WalletNameValidator
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import kotlinx.android.synthetic.main.card_create_wallet_name.*
import kotlinx.android.synthetic.main.card_create_wallet_password.*

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

    val walletCreationPasswordData by lazy {
        ViewModelProviders.of(activity!!)
                .get(WalletCreationPasswordViewModel::class.java)
                .walletCreationPassword
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walletNameValidator = WalletNameValidator(createWalletNameInputLayout) { this.onFormChanged() }
        val passwordValidator = PasswordValidator(createWalletPasswordInputLayout) { this.onFormChanged() }
        validatorList = listOf(passwordValidator)

        createButton.setOnClickListener {
            if (isAllValidatorsValid()) {
                val walletName = walletNameValidator.getText()
                val password = passwordValidator.getText()

                if (!listOf(walletName, password).allNonnull()) {
                    loge("Error! This should not have occurred", IllegalStateException())
                    return@setOnClickListener
                }

                walletCreationPasswordData.value = WalletCreationPassword(walletName!!, password!!)

                ConfirmPasswordDialog().show(fragmentManager, ConfirmPasswordDialog.TAG)
            }
        }
    }

    // Mark - Private Methods

    private fun onFormChanged() {
        createButton.isEnabled = isAllValidatorsValid()
    }

}