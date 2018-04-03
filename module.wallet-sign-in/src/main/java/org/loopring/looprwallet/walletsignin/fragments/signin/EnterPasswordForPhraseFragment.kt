package org.loopring.looprwallet.walletsignin.fragments.signin

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.walletsignin.dialogs.ConfirmPasswordDialog
import org.loopring.looprwallet.core.extensions.loge
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.walletsignin.fragments.CreateWalletRememberPhraseFragment
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationPhrase
import org.loopring.looprwallet.core.utilities.ViewUtility
import org.loopring.looprwallet.core.validators.PasswordValidator
import org.loopring.looprwallet.core.validators.WalletNameValidator
import com.caplaninnovations.looprwallet.viewmodels.wallet.WalletGeneratorViewModel

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

        fun createRestorationInstance() =
                EnterPasswordForPhraseFragment().apply {
                    arguments = bundleOf(KEY_TYPE to TYPE_RESTORE_WALLET)
                }

        fun createCreationInstance() =
                EnterPasswordForPhraseFragment().apply {
                    arguments = bundleOf(KEY_TYPE to TYPE_CREATE_WALLET)
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
            show()
        }

        walletGeneratorViewModel.createPhraseAsync(walletName, password)
    }

    // MARK - Private Methods

    private fun setupViewModel(view: View) {
        walletGeneratorViewModel.walletCreation.observeForDoubleSpend(this, {
            val progress = (activity as? BaseActivity)?.progressDialog
            if (progress?.isShowing == true) {
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
    }

    private fun onSubmitFormClick(buttonView: View) {
        val walletName = walletNameEditText.text.toString()
        val password = walletPasswordEditText.text.toString()

        ViewUtility.closeKeyboard(buttonView)

        val wallet = WalletCreationPhrase(walletName, password, arrayListOf())

        when (fragmentType) {
            TYPE_CREATE_WALLET -> {
                val dialog = ConfirmPasswordDialog.createInstance(TAG, wallet)
                dialog.show(fragmentManager, ConfirmPasswordDialog.TAG)
            }

            TYPE_RESTORE_WALLET -> {
                pushFragmentTransaction(
                        SignInEnterPhraseFragment.createEnterPhraseInstance(wallet),
                        SignInEnterPhraseFragment.TAG
                )
            }

            else -> throw IllegalArgumentException("Invalid fragment type, found: $fragmentType")
        }
    }

}