package org.loopring.looprwallet.walletsignin.fragments.createwallet

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.runner.AndroidJUnit4
import kotlinx.android.synthetic.main.fragment_create_wallet_selection.*
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.fragments.signin.EnterPasswordForPhraseFragment

/**
 * Created by Corey on 3/5/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CreateWalletSelectionFragmentTest : BaseDaggerFragmentTest<CreateWalletSelectionFragment>() {

    override fun provideFragment() = CreateWalletSelectionFragment()
    override val tag = CreateWalletSelectionFragment.TAG

    @Test
    fun click_createWalletKeystoreFragment() {
        Espresso.onView(Matchers.`is`(fragment.createFromKeystoreButton))
                .perform(ViewActions.click())

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(CreateWalletKeystoreFragment.TAG, fragment.tag)
    }

    @Test
    fun click_createWalletPhraseFragment() {
        Espresso.onView(Matchers.`is`(fragment.createFromPhraseButton))
                .perform(ViewActions.click())

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(EnterPasswordForPhraseFragment.TAG, fragment.tag)
    }

}