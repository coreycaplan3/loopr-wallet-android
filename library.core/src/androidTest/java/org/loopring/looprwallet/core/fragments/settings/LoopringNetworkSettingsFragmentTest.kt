package org.loopring.looprwallet.core.fragments.settings

import android.support.test.runner.AndroidJUnit4
import android.support.v7.preference.ListPreference
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class LoopringNetworkSettingsFragmentTest : BaseDaggerFragmentTest<LoopringNetworkSettingsFragment>() {

    override fun provideFragment() = LoopringNetworkSettingsFragment()
    override val tag = LoopringNetworkSettingsFragment.TAG

    @Test
    fun selectRelay() = runBlockingUiCode {
        val key = LoopringNetworkSettings.KEY_RELAY
        val value = LoopringNetworkSettings.DEFAULT_VALUE_RELAY

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

    @Test
    fun selectContractVersion() = runBlockingUiCode {
        val key = LoopringNetworkSettings.KEY_CONTRACT_VERSION
        val value = LoopringNetworkSettings.DEFAULT_VALUE_CONTRACT_VERSION

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

}