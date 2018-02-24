package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.handlers.WalletCreationHandler
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.utilities.snackbar
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PrivateKeyValidator(privateKeyInputLayout, this::onFormChanged)
        )

        privateKeyUnlockButton.setOnClickListener {
            val walletName = walletNameEditText.text.toString()
            val privateKey = privateKeyEditText.text.toString()
            val securityClient = (activity as? BaseActivity)?.securityClient

            val credentials = Credentials.create(privateKey)

            when {
                securityClient != null -> {
                    WalletCreationHandler(walletName, credentials, securityClient)
                            .createWallet(it)
                }
                else -> {
                    loge("Could not create wallet!", IllegalStateException())
                    view.snackbar(R.string.error_creating_wallet)
                }
            }
        }
    }

    override fun onFormChanged() {
        privateKeyUnlockButton.isEnabled = isAllValidatorsValid()
    }

}