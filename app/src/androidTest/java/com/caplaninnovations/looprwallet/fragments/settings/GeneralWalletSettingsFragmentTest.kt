package com.caplaninnovations.looprwallet.fragments.settings

import android.support.v14.preference.SwitchPreference
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.GeneralWalletSettings
import org.junit.Test

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class GeneralWalletSettingsFragmentTest : BaseDaggerFragmentTest<GeneralWalletSettingsFragment>() {

    override val fragment = GeneralWalletSettingsFragment()
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