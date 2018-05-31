package org.loopring.looprwallet.core.models.settings

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.int
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import java.math.BigDecimal

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Settings that are specific to Loopring. Some examples include user defaults
 * for margin splitting and LRC fees
 */
class LoopringFeeSettings(private val looprSettings: LooprSettings) {

    companion object {

        val KEY_LRC_FEE_PERCENTAGE = str(R.string.settings_loopring_fees_lrc_fee_percentage_key)
        val KEY_MARGIN_SPLIT_PERCENTAGE = str(R.string.settings_loopring_fees_margin_split_percentage_key)

        val DEFAULT_VALUE_LRC_FEE_PERCENTAGE = str(R.string.settings_loopring_fees_lrc_fee_percentage_default_value)
        val DEFAULT_VALUE_MARGIN_SPLIT_PERCENTAGE = int(R.integer.settings_loopring_fees_margin_split_percentage_default_value).toString()
    }

    /**
     * The current LRC fee percentage. The default and min value is 0.002 or 0.2%
     */
    val currentLrcFeePercentage: BigDecimal
        get() = getValue(KEY_LRC_FEE_PERCENTAGE, DEFAULT_VALUE_LRC_FEE_PERCENTAGE)

    /**
     * The current margin split, expressed as a decimal number (dividing the percentage by 100).
     * An example value is 0.50
     */
    val currentMarginSplit: BigDecimal
        get() = getValue(KEY_MARGIN_SPLIT_PERCENTAGE, DEFAULT_VALUE_MARGIN_SPLIT_PERCENTAGE) / BigDecimal(100)

    // MARK - Private Methods

    private fun getValue(key: String, defaultValue: String): BigDecimal {
        return BigDecimal(looprSettings.getString(key) ?: defaultValue)
    }

}