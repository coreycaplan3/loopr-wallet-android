package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.viewmodels.WalletGeneratorViewModel
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.WalletGeneratorUtility
import com.caplaninnovations.looprwallet.validators.PrivateKeyValidator
import com.caplaninnovations.looprwallet.validators.WalletNameValidator
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_restore_private_key.*
import org.web3j.crypto.Credentials

/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class RestoreWalletPrivateKeyFragment : BaseFragment() {

    companion object {
        val TAG: String = RestoreWalletPrivateKeyFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_restore_private_key

    private val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PrivateKeyValidator(privateKeyInputLayout, this::onFormChanged)
        )

        privateKeyUnlockButton.setOnClickListener { onUnlockWalletClick() }

        WalletGeneratorUtility.setupForFragment(walletGeneratorViewModel, this)
    }

    override fun onFormChanged() {
        privateKeyUnlockButton.isEnabled = isAllValidatorsValid()
    }

    // MARK - Private Methods

    /**
     * Called when the user attempts to create the wallet with the given private key.
     *
     * @param view The button view that was clicked
     */
    private fun onUnlockWalletClick() {
        val walletName = walletNameEditText.text.toString()
        val privateKey = privateKeyEditText.text.toString().toLowerCase()

        walletGeneratorViewModel.createCredentialsWallet(walletName, privateKey)
    }

}