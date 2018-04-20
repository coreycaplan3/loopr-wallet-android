package org.loopring.looprwallet.core.models.cryptotokens

import org.loopring.looprwallet.core.models.TrackedRealmObject
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import io.realm.RealmList
import io.realm.RealmModel
import org.loopring.looprwallet.core.extensions.equalTo
import java.math.BigDecimal
import java.math.BigInteger

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
     * The human-readable name of the token. For example, Loopring
     */
    val name: String

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
    var ticker: String

    /**
     * One token can have many balances from different wallets
     */
    var tokenBalances: RealmList<TokenBalanceInfo>

    /**
     * The price of the token currently in USD. This number always has a radix of size 10. To get
     * the decimal representation of this number, divide it by 100.
     */
    var priceInUsd: BigInteger?

    /**
     * The price of the token currently in the user's native currency. This number always has a
     * radix of size 10. To get the decimal representation of this number, divide it by 100.
     */
    var priceInNativeCurrency: BigInteger?

    fun getBalanceOf(address: String) = tokenBalances.find { it.address == address }?.balance

    /**
     * Formats the total supply so it can be presented to the user.
     *
     * @param currencySettings An instance of [CurrencySettings] used to format the total supply
     * with the user's settings.s
     * @return A string representing the total supply of the [CryptoToken] that
     */
    fun formatTotalSupply(currencySettings: CurrencySettings): String {
        val format = currencySettings.getNumberFormatter()
        val supply = (totalSupply.setScale(decimalPlaces)) / (BigDecimal(10).pow(decimalPlaces))
        return format.format(supply)
    }

    /**
     * Finds a given address's [TokenBalanceInfo] or null if it cannot be found
     */
    fun findAddressBalance(address: String): TokenBalanceInfo? {
        return this.tokenBalances.where()
                .equalTo(TokenBalanceInfo::address, address)
                .findFirst()
    }

}