package com.caplaninnovations.looprwallet.models.crypto

import com.caplaninnovations.looprwallet.models.TrackedRealmObject
import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import io.realm.RealmModel
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface CryptoToken : TrackedRealmObject, RealmModel {

    /**
     * The identifier used to uniquely distinguish between different crypto. For example, on
     * Ethereum this could be a contract address (for ERC-20 tokens).
     */
    val identifier: String

    /**
     * The token's total supply, represented as a BigDecimal. It has the format of
     * 250,000,000,000,000,000. To convert it to a number with proper decimals intact (the "real"
     * total supply), divide this number by *Math.pow(10, [decimalPlaces])*.
     */
    var totalSupply: BigDecimal

    /**
     * The number of decimal places that the token has. Used for dividing the [totalSupply] from
     * a large whole number to be a decimal.
     *
     * @see totalSupply
     */
    var decimalPlaces: Int

    /**
     * The 3 to 7 letter symbol that represents the crypto
     */
    val ticker: String

    var balance: BigDecimal?

    /**
     * The price of the token, in terms of USD.
     */
    var priceInUsd: BigDecimal?

    /**
     * The price of the token in the user's currently-selected currency.
     * This variable should go out 8 decimal places.
     */
    var priceInNativeCurrency: BigDecimal?

    /**
     * Formats the total supply so it can be presented to the user.
     *
     * @param currencySettings An instance of [CurrencySettings] used to format the total supply
     * with the user's settings.s
     * @return A string representing the total supply of the [CryptoToken] that
     */
    fun formatTotalSupply(currencySettings: CurrencySettings): String {
        val format = currencySettings.getTokenInstance()
        val supply = (totalSupply.setScale(decimalPlaces)) / (BigDecimal(10).pow(decimalPlaces))
        return format.format(supply)
    }

}