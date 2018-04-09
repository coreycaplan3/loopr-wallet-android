package org.loopring.looprwallet.core.fragments.settings

import android.support.test.runner.AndroidJUnit4
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.SeekBarPreference
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class LoopringFeeSettingsFragmentTest : BaseDaggerFragmentTest<LoopringFeeSettingsFragment>() {

    override fun provideFragment() = LoopringFeeSettingsFragment()
    override val tag = LoopringFeeSettingsFragment.TAG

    @Test
    fun changeLrcTradeFee() = runBlockingUiCode {
        val key = LoopringFeeSettings.KEY_LRC_FEE
        val preference = fragment.findPreference(key) as EditTextPreference
        preference.text = "5.5"

        checkPreferenceKeyAndValue(key, "5.5")
    }

    @Test
    fun changeMarginSplitFee() = runBlockingUiCode {
        val key = LoopringFeeSettings.KEY_MARGIN_SPLIT
        val preference = fragment.findPreference(key) as SeekBarPreference
        preference.value = 50

        checkPreferenceKeyAndValue(key, 50)
    }


}