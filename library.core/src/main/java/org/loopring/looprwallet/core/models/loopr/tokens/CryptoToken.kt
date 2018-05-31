package org.loopring.looprwallet.core.models.loopr.tokens

import io.realm.RealmList
import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigInteger

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface CryptoToken : TrackedRealmObject {

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
     * The token's total supply, represented as a BigInteger. It has the padding of all decimal
     * places in [decimalPlaces] (10^*decimalPlaces*).
     */
    var totalSupply: BigInteger

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
     * The list of balances mapping to an address owning the balance. If this object was
     * retrieved from the network, the first index is the newly retrieved balance for that
     * address.
     */
    var tokenBalances: RealmList<TokenBalanceInfo>

    /**
     * The list of allowances mapping to a wallet address owning having that allowance. If this
     * object was retrieved from the network, the first index is the newly retrieved allowance
     * for that address.
     */
    var tokenAllowances: RealmList<TokenAllowanceInfo>

    /**
     * The list of prices mapping to a currency having that price. If this object was retrieved
     * from the network, the first index is the newly retrieved price for that currency. All prices
     * are formatted using **4** decimal places
     */
    var tokenPrices: RealmList<TokenPriceInfo>

    /**
     * Finds a given address's [TokenBalanceInfo] or null if it cannot be found
     */
    fun findAddressBalance(address: String): BigInteger? {
        return tokenBalances.find { it.address == address }?.balance
    }

    /**
     * Finds a given address's [TokenAllowanceInfo] or null if it cannot be found
     */
    fun findAddressAllowance(address: String): BigInteger? {
        return tokenAllowances.find { it.address == address }?.allowance
    }

    /**
     * Finds a given currency's [TokenPriceInfo] or null if it cannot be found
     *
     * @see CurrencySettings
     */
    fun findCurrencyPrice(currency: String): BigInteger? {
        return tokenPrices.find { it.currency == currency }?.price
    }

}