package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.support.test.runner.AndroidJUnit4
import kotlinx.android.synthetic.main.fragment_create_wallet_phrase_remember.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.BuildConfig
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationPhrase

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

    override fun provideFragment(): CreateWalletRememberPhraseFragment {
        val wallet = WalletCreationPhrase(walletName, password, phrase)
        return CreateWalletRememberPhraseFragment.createInstance(wallet)
    }

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
        assertEquals(fragment.phraseRememberLabel.text, phrase.joinToString("\n") {
            it
        })
    }

}