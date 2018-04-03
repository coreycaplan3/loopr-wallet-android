package org.loopring.looprwallet.core.models.settings

import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.utilities.ApplicationUtility.bool
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str

/**
 * Created by Corey on 3/25/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: Settings that can be applied generally to all wallets on the device. Some
 * examples include hiding 0-balance tokens by default.
 */
class GeneralWalletSettings(private val looprSettings: LooprSettings) {

    companion object {

        val KEY_SHOW_ZERO_BALANCES = str(R.string.settings_general_wallet_zero_balance_key)

        val DEFAULT_VALUE_SHOW_ZERO_BALANCES = bool(R.bool.settings_general_wallet_zero_balance_default_value).toString()
    }

}