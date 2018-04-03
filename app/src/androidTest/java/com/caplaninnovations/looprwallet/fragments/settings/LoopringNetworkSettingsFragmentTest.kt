package com.caplaninnovations.looprwallet.fragments.settings

import android.support.v7.preference.ListPreference
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LoopringNetworkSettings
import org.junit.Test

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class LoopringNetworkSettingsFragmentTest : BaseDaggerFragmentTest<LoopringNetworkSettingsFragment>() {

    override val fragment = LoopringNetworkSettingsFragment()

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