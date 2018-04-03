package com.caplaninnovations.looprwallet.fragments.restorewallet

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.walletsignin.fragments.signin.EnterPasswordForPhraseFragment
import kotlinx.android.synthetic.main.fragment_restore_wallet_selection.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test
import org.loopring.looprwallet.walletsignin.fragments.restorewallet.RestoreWalletKeystoreFragment
import org.loopring.looprwallet.walletsignin.fragments.restorewallet.RestoreWalletPrivateKeyFragment
import org.loopring.looprwallet.walletsignin.fragments.restorewallet.RestoreWalletSelectionFragment

/**
 * Created by Corey on 3/5/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class RestoreWalletSelectionFragmentTest : BaseDaggerFragmentTest<RestoreWalletSelectionFragment>() {

    override val fragment = RestoreWalletSelectionFragment()
    override val tag = RestoreWalletSelectionFragment.TAG

    @Test
    fun click_restoreWalletPrivateKeyFragment() {
        Espresso.onView(`is`(fragment.restoreWalletPrivateKeyButton))
                .perform(click())

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(RestoreWalletPrivateKeyFragment.TAG, fragment.tag)
    }

    @Test
    fun click_restoreWalletKeystoreFragment() {
        Espresso.onView(`is`(fragment.restoreWalletKeystoreButton))
                .perform(click())

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(RestoreWalletKeystoreFragment.TAG, fragment.tag)
    }

    @Test
    fun click_restoreWalletPhraseFragment() {
        Espresso.onView(`is`(fragment.restoreWalletPhraseButton))
                .perform(click())

        val fragment = activity.supportFragmentManager.findFragmentById(R.id.activityContainer)
        assertEquals(EnterPasswordForPhraseFragment.TAG, fragment.tag)
    }

}