package org.loopring.looprwallet.core.fragments.settings

import android.support.test.runner.AndroidJUnit4
import android.support.v7.preference.ListPreference
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.CurrencySettings

/**
 * Created by Corey Caplan on 3/29/18.
 *
 *  Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CurrencySettingsFragmentTest : BaseDaggerFragmentTest<CurrencySettingsFragment>() {

    override fun provideFragment() = CurrencySettingsFragment()
    override val tag = CurrencySettingsFragment.TAG

    @Test
    fun selectDefaultCurrency_everythingElseIsDisabled() = runBlockingUiCode {
        val key = CurrencySettings.KEY_CURRENT_CURRENCY
        val preference = fragment.findPreference(key) as ListPreference
        preference.value = CurrencySettings.DEFAULT_VALUE_CURRENCY

        fragment.getPreferenceKeysAndDefaultValues().forEach {
            val isCurrentCurrency = it.first == key

            val p = fragment.findPreference(it.first)
            if (isCurrentCurrency) {
                assertTrue(p.isEnabled)
            } else {
                assertFalse(p.isEnabled)
            }
        }

        checkPreferenceKeyAndValue(key, CurrencySettings.DEFAULT_VALUE_CURRENCY)

        preference.value = CurrencySettings.CNY
        fragment.getPreferenceKeysAndDefaultValues().forEach {
            assertTrue(fragment.findPreference(it.first).isEnabled)
        }

        checkPreferenceKeyAndValue(key, CurrencySettings.CNY)
    }

    @Test
    fun selectRefreshFrequency() = runBlockingUiCode {
        val currentCurrencyPreference = fragment.findPreference(CurrencySettings.KEY_CURRENT_CURRENCY) as ListPreference
        currentCurrencyPreference.value = CurrencySettings.CNY

        val key = CurrencySettings.KEY_REFRESH_FREQUENCY
        val refreshFrequencyPreference = fragment.findPreference(key) as ListPreference

        val value = CurrencySettings.ARRAY_REFRESH_FREQUENCY[2]
        refreshFrequencyPreference.value = value

        checkPreferenceKeyAndValue(key, CurrencySettings.ARRAY_REFRESH_FREQUENCY[2])
    }

}