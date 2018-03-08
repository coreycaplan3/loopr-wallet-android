package com.caplaninnovations.looprwallet.fragments.createwallet

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import com.caplaninnovations.looprwallet.test.BuildConfig
import com.caplaninnovations.looprwallet.utilities.mkString
import kotlinx.android.synthetic.main.fragment_create_wallet_phrase_remember.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey on 3/5/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(AndroidJUnit4::class)
class CreateWalletRememberPhraseFragmentTest : BaseDaggerFragmentTest<CreateWalletRememberPhraseFragment>() {

    private val walletName = "loopr-wallet"
    private val password = "looprloopr"
    private val phrase = arrayListOf(
            "address",
            "stadium",
            "photo",
            "soup",
            "theory",
            "remind",
            "lonely",
            "category",
            "hat",
            "swear",
            "cause",
            "appear"
    )

    override val fragment = CreateWalletRememberPhraseFragment.createInstance(
            WalletCreationPhrase(walletName, password, phrase)
    )

    override val tag = CreateWalletRememberPhraseFragment.TAG

    @Test
    fun isSubmitButtonEnabled_checkTimeout() {

        runBlocking {
            assertFalse(fragment.rememberGeneratePhraseButton.isEnabled)

            delay(BuildConfig.DEFAULT_READ_TIMEOUT + 100L)

            assertTrue(fragment.rememberGeneratePhraseButton.isEnabled)
        }

    }

    @Test
    fun checkPhraseTextSameAsArgument() {
        assertEquals(fragment.phraseRememberLabel.text, phrase.mkString("\n"))
    }

}