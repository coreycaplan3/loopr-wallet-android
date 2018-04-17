package org.loopring.looprwallet.core.fragments.settings

import android.support.test.runner.AndroidJUnit4
import android.support.v14.preference.SwitchPreference
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.GeneralWalletSettings

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class GeneralWalletSettingsFragmentTest : BaseDaggerFragmentTest<GeneralWalletSettingsFragment>() {

    override fun provideFragment() = GeneralWalletSettingsFragment()
    override val tag = GeneralWalletSettingsFragment.TAG

    @Test
    fun changeShowZeroBalances() = runBlockingUiCode {
        val key = GeneralWalletSettings.KEY_SHOW_ZERO_BALANCES

        val preference = fragment.findPreference(key) as SwitchPreference
        preference.isChecked = true

        checkPreferenceKeyAndValue(key, "true")

        preference.isChecked = false

        checkPreferenceKeyAndValue(key, "false")
    }

}