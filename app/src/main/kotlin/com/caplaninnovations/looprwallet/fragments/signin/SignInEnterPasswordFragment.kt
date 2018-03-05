package com.caplaninnovations.looprwallet.fragments.signin

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.fragments.createwallet.CreateWalletRememberPhraseFragment
import com.caplaninnovations.looprwallet.models.android.observers.observeForDoubleSpend
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import com.caplaninnovations.looprwallet.utilities.logd
import com.caplaninnovations.looprwallet.utilities.longToast
import com.caplaninnovations.looprwallet.validators.PasswordValidator
import com.caplaninnovations.looprwallet.validators.WalletNameValidator
import com.caplaninnovations.looprwallet.viewmodels.WalletGeneratorViewModel
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_sign_in_enter_password.*

/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: First step in the create/restore phrase wallet process.
 *
 */
class SignInEnterPasswordFragment : BaseFragment() {

    companion object {
        val TAG: String = SignInEnterPasswordFragment::class.java.simpleName

        private const val KEY_TYPE = "_TYPE"

        const val TYPE_RESTORE_WALLET = "restore_wallet"
        const val TYPE_CREATE_WALLET = "create_wallet"

        fun createInstance(type: String) =
                SignInEnterPasswordFragment().apply {
                    arguments = Bundle().apply { putString(KEY_TYPE, type) }
                }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_sign_in_enter_password

    private val fragmentType by lazy {
        arguments!!.getString(KEY_TYPE)
    }

    private val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PasswordValidator(enterWalletPasswordInputLayout, this::onFormChanged)
        )

        when (fragmentType) {
            TYPE_CREATE_WALLET -> {
                enterWalletPasswordTitleLabel.setText(R.string.enter_a_strong_password)
                rememberPhraseConfirmButton.setText(R.string.generate_phrase)
                enterPhrasePasswordSafetyLabel.setText(R.string.safety_create_phrase)
            }
            TYPE_RESTORE_WALLET -> {
                enterWalletPasswordTitleLabel.setText(R.string.enter_old_password)
                rememberPhraseConfirmButton.setText(R.string.enter_phrase)
                enterPhrasePasswordSafetyLabel.setText(R.string.safety_recover_phrase)
            }
            else -> throw IllegalArgumentException("Invalid fragmentType, found: $fragmentType")
        }

        walletGeneratorViewModel.walletCreation.observeForDoubleSpend(this, {
            val progress = (activity as? BaseActivity)?.progressDialog
            if (progress?.isShowing == true) {
                logd("Dismissing progress dialog...")
                progress.dismiss()
            }

            if (it.error != null) {
                view.context.longToast(it.error)
            }
        })

        walletGeneratorViewModel.walletPhraseGeneration.observeForDoubleSpend(this, {
            pushFragmentTransaction(
                    CreateWalletRememberPhraseFragment.createInstance(it),
                    CreateWalletRememberPhraseFragment.TAG
            )
        })

        rememberPhraseConfirmButton.setOnClickListener { onSubmitFormClick() }
    }

    override fun onFormChanged() {
        rememberPhraseConfirmButton.isEnabled = isAllValidatorsValid()
    }

    // MARK - Private Methods

    private fun onSubmitFormClick() {
        val walletName = walletNameEditText.text.toString()
        val password = enterWalletPasswordEditText.text.toString()

        when (fragmentType) {
            TYPE_CREATE_WALLET -> {
                (activity as? BaseActivity)?.progressDialog?.apply {
                    setMessage(getString(R.string.generating_phrase))
                    show()
                }

                walletGeneratorViewModel.createPhraseAsync(walletName, password)
            }

            TYPE_RESTORE_WALLET -> {
                val wallet = WalletCreationPhrase(walletName, password, arrayListOf())
                pushFragmentTransaction(
                        SignInEnterPhraseFragment.createEnterPhraseInstance(wallet),
                        SignInEnterPhraseFragment.TAG
                )
            }

            else -> throw IllegalArgumentException("Invalid fragment type, found: $fragmentType")
        }
    }

}