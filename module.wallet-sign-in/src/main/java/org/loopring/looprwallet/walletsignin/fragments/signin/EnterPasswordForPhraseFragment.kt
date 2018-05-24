package org.loopring.looprwallet.walletsignin.fragments.signin

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.card_enter_wallet_password.*
import kotlinx.android.synthetic.main.card_wallet_name.*
import kotlinx.android.synthetic.main.fragment_sign_in_enter_password.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.android.fragments.FragmentTransactionController
import org.loopring.looprwallet.core.utilities.ViewUtility
import org.loopring.looprwallet.core.validators.PasswordValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.walletsignin.fragments.createwallet.CreateWalletRememberPhraseFragment
import org.loopring.looprwallet.walletsignin.models.WalletCreationPhrase
import org.loopring.looprwallet.walletsignin.models.WalletCreationResult
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel

/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: First step in the create/restore phrase wallet process.
 *
 */
class EnterPasswordForPhraseFragment : BaseFragment(), ConfirmPasswordDialog.OnPasswordConfirmedListener {

    companion object {
        val TAG: String = EnterPasswordForPhraseFragment::class.simpleName!!

        private const val KEY_TYPE = "_TYPE"

        private const val TYPE_RESTORE_WALLET = "restore_phrase_wallet"
        private const val TYPE_CREATE_WALLET = "create_phrase_wallet"

        fun getRestorationInstance() = EnterPasswordForPhraseFragment().apply {
            arguments = bundleOf(KEY_TYPE to TYPE_RESTORE_WALLET)
        }

        fun getCreationInstance() = EnterPasswordForPhraseFragment().apply {
            arguments = bundleOf(KEY_TYPE to TYPE_CREATE_WALLET)
        }

    }

    override val layoutResource: Int
        get() = R.layout.fragment_sign_in_enter_password

    private val fragmentType by lazy {
        arguments!!.getString(KEY_TYPE)
    }

    private val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        LooprViewModelFactory.get<WalletGeneratorViewModel>(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatorList = listOf(
                WalletNameValidator(walletNameInputLayout, this::onFormChanged),
                PasswordValidator(walletPasswordInputLayout, this::onFormChanged)
        )

        when (fragmentType) {
            TYPE_CREATE_WALLET -> {
                enterWalletPasswordTitleLabel.setText(R.string.enter_a_strong_password)
                rememberGeneratePhraseButton.setText(R.string.generate_phrase)
                enterPhrasePasswordSafetyLabel.setText(R.string.safety_create_phrase)
            }
            TYPE_RESTORE_WALLET -> {
                enterWalletPasswordTitleLabel.setText(R.string.enter_old_password)
                rememberGeneratePhraseButton.setText(R.string.enter_phrase)
                enterPhrasePasswordSafetyLabel.setText(R.string.safety_recover_phrase)
            }
            else -> throw IllegalArgumentException("Invalid fragmentType, found: $fragmentType")
        }

        rememberGeneratePhraseButton.setOnClickListener(this::onSubmitFormClick)

        setupViewModel(view)
    }

    override fun onFormChanged() {
        rememberGeneratePhraseButton.isEnabled = isAllValidatorsValid()
    }

    override fun onPasswordConfirmed() {
        val walletName = walletNameEditText.text.toString()
        val password = walletPasswordEditText.text.toString()

        if (!isAllValidatorsValid()) {
            loge("All validators should be valid!", IllegalStateException())
            return
        }

        (activity as? BaseActivity)?.progressDialog?.apply {
            setMessage(getString(R.string.generating_phrase))
        }

        walletGeneratorViewModel.createPhraseAsync(walletName, password)
    }

    // MARK - Private Methods

    private fun setupViewModel(view: View) {
        walletGeneratorViewModel.isTransactionRunning.observeForDoubleSpend(this) { isRunning ->
            val progress = (activity as? BaseActivity)?.progressDialog
            when {
                isRunning && progress != null && !progress.isShowing ->
                    progress.show()

                !isRunning && progress?.isShowing == true ->
                    progress.dismiss()
            }
        }

        walletGeneratorViewModel.result.observeForDoubleSpend(this) {
            (it as? WalletCreationPhrase)?.let {
                pushFragmentTransaction(
                        CreateWalletRememberPhraseFragment.getInstance(it),
                        CreateWalletRememberPhraseFragment.TAG,
                        FragmentTransactionController.ANIMATION_HORIZONTAL
                )
            }
        }

        walletGeneratorViewModel.error.observeForDoubleSpend(this) {
            view.context.longToast(WalletGeneratorViewModel.getMessageFromError(it))
        }
    }

    private fun onSubmitFormClick(buttonView: View) {
        val walletName = walletNameEditText.text.toString()
        val password = walletPasswordEditText.text.toString()

        ViewUtility.closeKeyboard(buttonView)

        val wallet = WalletCreationPhrase(walletName, password, arrayListOf())

        when (fragmentType) {
            TYPE_CREATE_WALLET -> {
                val dialog = ConfirmPasswordDialog.getInstance(TAG, password)
                dialog.show(fragmentManager, ConfirmPasswordDialog.TAG)
            }

            TYPE_RESTORE_WALLET -> {
                pushFragmentTransaction(
                        SignInEnterPhraseFragment.createEnterPhraseInstance(wallet),
                        SignInEnterPhraseFragment.TAG,
                        FragmentTransactionController.ANIMATION_HORIZONTAL
                )
            }

            else -> throw IllegalArgumentException("Invalid fragment type, found: $fragmentType")
        }
    }

}