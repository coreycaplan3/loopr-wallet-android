package com.caplaninnovations.looprwallet.fragments.settings

import android.support.v7.preference.ListPreference
import android.support.v7.preference.SeekBarPreference
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.junit.Test

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class EthereumFeeSettingsFragmentTest : BaseDaggerFragmentTest<EthereumFeeSettingsFragment>() {

    override val fragment = EthereumFeeSettingsFragment()

    override val tag = EthereumFeeSettingsFragment.TAG

    @Test
    fun changeGasPrice() = runBlockingUiCode {
        val key = EthereumFeeSettings.KEY_GAS_PRICE
        val value = 40

        val preference = fragment.findPreference(key) as SeekBarPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value.toString())
    }

    @Test
    fun changeTransferEtherGasLimit() = runBlockingUiCode {
        val key = EthereumFeeSettings.KEY_TRANSFER_ETHER_GAS_LIMIT
        val value = "40000"

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

    @Test
    fun changeTransferTokenGasLimit() = runBlockingUiCode {
        val key = EthereumFeeSettings.KEY_TRANSFER_TOKEN_GAS_LIMIT
        val value = "60000"

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

}