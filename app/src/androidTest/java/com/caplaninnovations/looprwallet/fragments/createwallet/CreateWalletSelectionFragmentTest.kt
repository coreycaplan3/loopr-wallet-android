package com.caplaninnovations.looprwallet.fragments.createwallet

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.fragments.signin.EnterPasswordForPhraseFragment
import kotlinx.android.synthetic.main.fragment_create_wallet_selection.*
import org.hamcrest.Matchers
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Corey on 3/5/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class CreateWalletSelectionFragmentTest : BaseDaggerFragmentTest<CreateWalletSelectionFragment>() {

    override val fragment = CreateWalletSelectionFragment()
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