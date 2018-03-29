package com.caplaninnovations.looprwallet.fragments.settings

import android.support.v7.preference.ListPreference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.android.settings.ThemeSettings
import kotlinx.coroutines.experimental.delay
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

    @Test
    fun changeTheme() {
        val key = ThemeSettings.KEY_THEME
        val lightTheme = ThemeSettings.LIGHT_THEME

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = lightTheme

        checkPreferenceKeyAndValue(key, lightTheme)
    }

    @Test
    fun goto_security() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_SECURITY

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, SecuritySettingsFragment.TAG)
    }

    @Test
    fun goto_ethereumFees() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_ETHEREUM_FEES

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, EthereumFeeSettingsFragment.TAG)
    }

    @Test
    fun goto_loopringFees() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_LOOPRING_FEES

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, LoopringFeeSettingsFragment.TAG)
    }

    @Test
    fun goto_generalWallet() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_GENERAL_WALLET

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, GeneralWalletSettingsFragment.TAG)
    }

    @Test
    fun goto_currency() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_CURRENCY

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, CurrencySettingsFragment.TAG)
    }

    @Test
    fun goto_ethereumNetwork() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_ETHEREUM_NETWORK

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, EthereumNetworkSettingsFragment.TAG)
    }

    @Test
    fun goto_loopringNetwork() = runBlockingUiCode {
        val key = HomeSettingsFragment.SCREEN_KEY_LOOPRING_NETWORK

        fragment.findPreference(key).performClick()

        delay(300L)

        checkCurrentFragmentByContainer(R.id.activityContainer, LoopringNetworkSettingsFragment.TAG)
    }


}