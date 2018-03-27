package com.caplaninnovations.looprwallet.fragments.security

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import kotlinx.android.synthetic.main.number_pad.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Corey on 3/27/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class ConfirmOldSecurityFragmentTest : BaseDaggerFragmentTest<ConfirmOldSecurityFragment>() {

    override val fragment = ConfirmOldSecurityFragment.createUnlockAppInstance()
    override val tag = ConfirmOldSecurityFragment.TAG

    @Before
    fun setUp() {
        fragment.userPinSettings.setUserPin("1234")
    }

    @Test
    fun enterIncorrectPin() {
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)
        clickView(fragment.numberPadOne)

        assertTrue(fragment.currentPin.isEmpty())
        assertFalse(fragment.userPinSettings.isUserLockedOut())
    }

}