package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_create_wallet_phrase_remember.*
import org.loopring.looprwallet.core.BuildConfig.DEFAULT_READ_TIMEOUT
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.fragments.signin.SignInEnterPhraseFragment
import org.loopring.looprwallet.walletsignin.models.WalletCreationPhrase

/**
 * Created by Corey on 3/4/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateWalletRememberPhraseFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateWalletRememberPhraseFragment::class.java.simpleName
        private const val KEY_WALLET_PHRASE = "_WALLET_PHRASE"

        private const val KEY_TIME_LEFT = "_TIME_LEFT"

        fun getInstance(walletCreationPhrase: WalletCreationPhrase) =
                CreateWalletRememberPhraseFragment().apply {
                    arguments = bundleOf(KEY_WALLET_PHRASE to walletCreationPhrase)
                }
    }

    private var timeLeft: Long = 0

    private val timer: CountDownTimer by lazy {
        object : CountDownTimer(timeLeft, 100) {
            override fun onFinish() {
                rememberGeneratePhraseButton.isEnabled = true
                rememberGeneratePhraseButton.setText(R.string.confirm_phrase)
            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateConfirmButton()
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_wallet_phrase_remember

    private val walletCreationPhrase: WalletCreationPhrase by lazy {
        arguments!!.getParcelable(KEY_WALLET_PHRASE) as WalletCreationPhrase
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phraseRememberLabel.text = walletCreationPhrase.phrase.joinToString("\n") {
            it
        }

        setupTimer(savedInstanceState)

        rememberGeneratePhraseButton.setOnClickListener {
            pushFragmentTransaction(
                    SignInEnterPhraseFragment.createConfirmPhraseInstance(walletCreationPhrase),
                    SignInEnterPhraseFragment.TAG
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(KEY_TIME_LEFT, timeLeft)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

    // Mark - Private Methods

    private fun setupTimer(savedInstanceState: Bundle?) {
        timeLeft = savedInstanceState?.getLong(KEY_TIME_LEFT, DEFAULT_READ_TIMEOUT) ?: DEFAULT_READ_TIMEOUT

        if (timeLeft > 0) timer.start()
        else timer.onFinish()
    }

    private fun updateConfirmButton() {
        rememberGeneratePhraseButton.isEnabled = false
        rememberGeneratePhraseButton.text = (timeLeft / 1000L).toString()
    }

}