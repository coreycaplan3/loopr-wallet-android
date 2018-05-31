package org.loopring.looprwallet.core.fragments.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.DEFAULT_VALUE_LRC_FEE_PERCENTAGE
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.DEFAULT_VALUE_MARGIN_SPLIT_PERCENTAGE
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.KEY_LRC_FEE_PERCENTAGE
import org.loopring.looprwallet.core.models.settings.LoopringFeeSettings.Companion.KEY_MARGIN_SPLIT_PERCENTAGE
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreLooprComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.settings_loopring_fees)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            KEY_LRC_FEE_PERCENTAGE to DEFAULT_VALUE_LRC_FEE_PERCENTAGE,
            KEY_MARGIN_SPLIT_PERCENTAGE to DEFAULT_VALUE_MARGIN_SPLIT_PERCENTAGE
    )

    override fun onPreferenceValueChange(preference: Preference, value: String) = true

    override fun getSummaryValue(preference: Preference, value: String): String = when (preference.key) {
        KEY_LRC_FEE_PERCENTAGE -> currencySettings.formatNumber(value.toDouble())
        KEY_MARGIN_SPLIT_PERCENTAGE -> "" // We don't bind a summary value for this SeekBar
        else -> throw IllegalArgumentException("Invalid preference key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?) = false

}