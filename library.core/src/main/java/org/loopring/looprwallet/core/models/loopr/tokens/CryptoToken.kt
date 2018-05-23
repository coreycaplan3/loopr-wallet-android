package org.loopring.looprwallet.core.models.loopr.tokens

import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import io.realm.RealmList
import org.loopring.looprwallet.core.extensions.formatAsToken
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
     * Finds a given address's [TokenBalanceInfo] or null if it cannot be found
     */
    fun findAddressBalance(address: String): TokenBalanceInfo? {
        return tokenBalances.find { it?.address == address }
    }

}