package com.caplaninnovations.looprwallet.fragments.createwallet

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.FragmentTestActivity
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.wallet.WalletCreationPassword
import com.caplaninnovations.looprwallet.utilities.CustomViewAssertions.Companion.isDisabled
import junit.framework.Assert.*
import kotlinx.android.synthetic.main.card_create_wallet_name.*
import kotlinx.android.synthetic.main.card_create_wallet_password.*
import kotlinx.android.synthetic.main.fragment_create_wallet_keystore.*
import org.hamcrest.Matchers.*
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey on 2/20/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CreateWalletKeystoreFragmentTest : BaseDaggerTest() {

    @get:Rule
    val activityRule = ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java)

    private val createWalletKeystoreFragment = CreateWalletKeystoreFragment()

    private val goodName = "corey"
    private val goodPassword = "corey"

    private val badName = "corey corey" // there cannot be any spaces
    private val badPassword = "corey" // it's too short

    private val nullString: String? = null

    @Before
    fun setUp() {
        activityRule.activity.addFragment(createWalletKeystoreFragment, CreateWalletKeystoreFragment.TAG)
    }

    @Test
    fun formIsEmpty_stateShouldBeEmpty() {
        onView(`is`(createWalletKeystoreFragment.createWalletNameEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun okayName_passwordEmpty() {
        onView(`is`(createWalletKeystoreFragment.createWalletNameEditText))
                .perform(typeText(goodName))
                .check(matches(hasErrorText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createWalletPasswordEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameEmpty_passwordOkay() {
        onView(`is`(createWalletKeystoreFragment.createWalletNameEditText))
                .check(matches(hasErrorText(nullString)))
                .check(matches(withText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createWalletPasswordEditText))
                .perform(typeText(goodPassword))
                .check(matches(hasErrorText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameBad_passwordBad() {
        onView(`is`(createWalletKeystoreFragment.createWalletNameEditText))
                .perform(typeText(badName))

        assertNotNull(createWalletKeystoreFragment.createWalletNameEditText.error)

        onView(`is`(createWalletKeystoreFragment.createWalletPasswordEditText))
                .perform(typeText(badPassword))

        assertNotNull(createWalletKeystoreFragment.createWalletPasswordEditText.error)

        onView(`is`(createWalletKeystoreFragment.createButton))
                .check(isDisabled())
    }

    @Test
    fun nameOkay_passwordOkay() {
        onView(`is`(createWalletKeystoreFragment.createWalletNameInputLayout.editText))
                .perform(typeText(goodName))
                .check(matches(hasErrorText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createWalletPasswordInputLayout.editText))
                .perform(typeText(goodPassword))
                .check(matches(hasErrorText(nullString)))

        onView(`is`(createWalletKeystoreFragment.createButton))
                .check(matches(isEnabled()))
                .perform(click())

        val walletCreationData = WalletCreationPassword(goodName, goodPassword)
        assertEquals(createWalletKeystoreFragment.walletCreationPasswordData.value, walletCreationData)
    }

}