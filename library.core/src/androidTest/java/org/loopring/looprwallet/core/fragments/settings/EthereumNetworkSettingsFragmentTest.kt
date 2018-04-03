package org.loopring.looprwallet.core.fragments.settings

import android.support.v7.preference.ListPreference
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.EthereumNetworkSettings
import org.junit.Test

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthereumNetworkSettingsFragmentTest : BaseDaggerFragmentTest<EthereumNetworkSettingsFragment>() {

    override val fragment = EthereumNetworkSettingsFragment()

    override val tag = EthereumNetworkSettingsFragment.TAG

    @Test
    fun changeNetworkSettings() = runBlockingUiCode {
        val key = EthereumNetworkSettings.KEY_NODE
        val value = EthereumNetworkSettings.ARRAY_NODE[2]

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

}