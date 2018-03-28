package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility.str
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

        val KEY_LRC_FEE = str(R.string.settings_loopring_fees_lrc_fee_key)
        val KEY_MARGIN_SPLIT = str(R.string.settings_loopring_fees_margin_split_key)

        val DEFAULT_VALUE_LRC_FEE = str(R.string.settings_loopring_fees_lrc_fee_default_value)
        val DEFAULT_VALUE_MARGIN_SPLIT = str(R.string.settings_loopring_fees_margin_split_default_value)
    }

    val currentLrcFee: BigDecimal
        get() = getValue(KEY_LRC_FEE, DEFAULT_VALUE_LRC_FEE)

    val currentMarginSplit: BigDecimal
        get() = getValue(KEY_MARGIN_SPLIT, DEFAULT_VALUE_MARGIN_SPLIT)

    // MARK - Private Methods

    private fun getValue(key: String, defaultValue: String): BigDecimal {
        return BigDecimal(looprSettings.getString(key) ?: defaultValue)
    }

}