package org.loopring.looprwallet.settings.fragments

import android.os.Bundle
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.DEFAULT_VALUE_LRC_FEE
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.DEFAULT_VALUE_MARGIN_SPLIT
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.KEY_LRC_FEE
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.KEY_MARGIN_SPLIT
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import javax.inject.Inject

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class LoopringFeeSettingsFragment : BaseSettingsFragment() {

    companion object {

        val TAG: String = LoopringFeeSettingsFragment::class.java.simpleName
    }

    override val fragmentTitle = str(R.string.trading_fees)

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_loopring_fees)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            KEY_LRC_FEE to DEFAULT_VALUE_LRC_FEE,
            KEY_MARGIN_SPLIT to DEFAULT_VALUE_MARGIN_SPLIT
    )

    override fun onPreferenceValueChange(preference: Preference, value: String) = true

    override fun getSummaryValue(preference: Preference, value: String): String = when (preference.key) {
        KEY_LRC_FEE -> currencySettings.getNumberFormatter().format(value.toDouble())
        KEY_MARGIN_SPLIT -> "" // We don't bind a summary value for this SeekBar
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

}