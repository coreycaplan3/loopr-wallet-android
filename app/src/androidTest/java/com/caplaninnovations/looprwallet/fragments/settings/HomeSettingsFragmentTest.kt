package com.caplaninnovations.looprwallet.fragments.settings

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class HomeSettingsFragmentTest : BaseDaggerFragmentTest<HomeSettingsFragment>() {

    override val fragment = HomeSettingsFragment()

    override val tag = HomeSettingsFragment.TAG

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun changeTheme() {
        val key = ThemeSettings.KEY_THEME
        val lightTheme = ThemeSettings.LIGHT_THEME

        val preference = fragment.findPreference(key)
        assertTrue(fragment.onPreferenceChange(preference, lightTheme))

        checkPreferenceKeyAndValue(key, lightTheme)
    }

    @Test
    fun goto_security() {

    }

    @Test
    fun goto_ethereumFees() {
    }

    @Test
    fun goto_loopringFees() {
    }

    @Test
    fun goto_generalWallet() {
    }

    @Test
    fun goto_currency() {
    }

    @Test
    fun goto_ethereumNetwork() {
    }

    @Test
    fun goto_loopringNetwork() {
    }


}