package org.loopring.looprwallet.core.utilities

import org.loopring.looprwallet.core.extensions.insertOrUpdate
import org.loopring.looprwallet.core.extensions.update
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenBalanceInfo
import java.math.BigInteger

object LooprTokenUtility {

    /**
     * Given an [oldToken] (from the realm) and a [newToken] (from network), does the following:
     * - If the [oldToken] is **not** null, returns the [oldToken], with the fields matching the
     * [newToken].
     * - If the [oldToken] is null, returns the [newToken].
     */
    fun mapTokenToBalanceAndReturnToken(newToken: LooprToken, oldToken: LooprToken?, address: String?): LooprToken {

        val tokenBalance = when {
            newToken.tokenBalances.isNotEmpty() -> newToken.tokenBalances[0]
            address != null -> TokenBalanceInfo().apply {
                this.address = address
                this.balance = BigInteger.ZERO
            }
            else -> null
        }

        return when {
            oldToken != null -> {
                oldToken.tokenBalances.insertOrUpdate(tokenBalance) {
                    it.address == address
                }
                oldToken
            }
            else ->
                // The token doesn't exist or it doesn't have an address. Return the new one
                newToken
        }

    }

}