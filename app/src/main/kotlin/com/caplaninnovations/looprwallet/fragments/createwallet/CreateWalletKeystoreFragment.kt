package com.caplaninnovations.looprwallet.fragments.createwallet

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.datalayer.WalletCreationPasswordViewModel
import com.caplaninnovations.looprwallet.dialogs.ConfirmPasswordDialog
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.PermissionHandler
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationKeystore
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

    private val filesPermissionHandler by lazy {
        PermissionHandler(
                activity as BaseActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionHandler.REQUEST_CODE_EXTERNAL_FILES,
                this::onFilePermissionGranted,
                this::onFilePermissionDenied
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walletNameValidator = WalletNameValidator(createWalletNameInputLayout) { this.onFormChanged() }
        val passwordValidator = PasswordValidator(createWalletPasswordInputLayout) { this.onFormChanged() }
        validatorList = listOf(walletNameValidator, passwordValidator)

        createButton.setOnClickListener {
            if (isAllValidatorsValid()) {
                val walletName = walletNameValidator.getText()
                val password = passwordValidator.getText()

                if (!listOf(walletName, password).allNonnull()) {
                    loge("Error! This should not have occurred", IllegalStateException())
                    return@setOnClickListener
                }

                walletCreationPasswordData.value = WalletCreationKeystore(walletName!!, password!!)

                filesPermissionHandler.requestPermission()
            }
        }
    }

    // Mark - Private Methods

    private fun onFormChanged() {
        createButton.isEnabled = isAllValidatorsValid()
    }

    private fun onFilePermissionGranted() {
        val walletCreationPassword = walletCreationPasswordData.value
        if (walletCreationPassword != null) {
            ConfirmPasswordDialog.createInstance(walletCreationPassword)
                    .show(fragmentManager, ConfirmPasswordDialog.TAG)
        } else {
            loge("Error invalid state!", IllegalStateException())
        }
    }

    private fun onFilePermissionDenied() {
        // TODO
    }

}