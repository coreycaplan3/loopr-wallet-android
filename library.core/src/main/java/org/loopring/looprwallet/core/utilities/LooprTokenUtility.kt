package org.loopring.looprwallet.core.utilities

import org.loopring.looprwallet.core.extensions.update
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenBalanceInfo

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
            address != null -> TokenBalanceInfo(address)
            else -> null
        }

        return when {
            oldToken != null -> {
                val doesTokenHaveAddressBalanceAlready = oldToken.tokenBalances.any { it.address == address }
                when {
                    tokenBalance != null && doesTokenHaveAddressBalanceAlready ->
                        // The token is in Realm and has a balance associated with it; update it in place
                        oldToken.tokenBalances.update(tokenBalance) { it.address == address }
                    tokenBalance != null ->
                        // The token is in Realm but DOES NOT have a balance associated with it; insert it
                        oldToken.tokenBalances.add(tokenBalance)
                }
                oldToken
            }
            else ->
                // The token doesn't exist or it doesn't have an address. Return the new one
                newToken
        }

    }

}