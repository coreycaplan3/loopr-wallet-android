package org.loopring.looprwallet.walletsignin.fragments.restorewallet

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_restore_private_key.*
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.validators.PrivateKeyValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel.Companion
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel.Companion.getMessageFromError

/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
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

        setupTransactionViewModel(walletGeneratorViewModel, R.string.creating_wallet, convertErrorToMessage = ::getMessageFromError)
    }

    override fun onFormChanged() {
        privateKeyUnlockButton.isEnabled = isAllValidatorsValid()
    }

    // MARK - Private Methods

    /**
     * Called when the user attempts to create the wallet with the given private key.
     */
    private fun onUnlockWalletClick() {
        val walletName = walletNameEditText.text.toString()
        val privateKey = privateKeyEditText.text.toString().toLowerCase()

        walletGeneratorViewModel.createPrivateKeyWallet(walletName, privateKey)
    }

}