package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.Case
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.notEqualTo
import org.loopring.looprwallet.core.extensions.sort
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.tokens.TokenBalanceInfo
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthTokenRepository : BaseRealmRepository() {

    /**
     * Finds [LooprToken.ETH] synchronously. If not found, it is inserted and returned from realm.
     *
     * @return An **UN-MANAGED** [LooprToken] that represents ETH.
     */
    fun getEthNow(context: HandlerContext = UI): LooprToken {
        val token = getRealmFromContext(context)
                .where<LooprToken>()
                .equalTo(LooprToken::ticker, LooprToken.ETH.ticker)
                .findFirst()

        return when {
            token != null -> getRealmFromContext(context).copyFromRealm(token)
            else -> LooprToken.ETH
        }
    }

    /**
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided contract
     * address.
     */
    fun getTokenByContractAddressNow(contractAddress: String, context: HandlerContext = UI): LooprToken? {
        return getRealmFromContext(context)
                .let { realm ->
                    val data = realm.where<LooprToken>()
                            .equalTo(LooprToken::identifier, contractAddress)
                            .findFirst() ?: return null

                    return@let realm.copyFromRealm(data)
                }
    }

    /**
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided ticker.
     */
    fun getTokenByTickerNow(ticker: String, context: HandlerContext = UI): LooprToken? {
        return getRealmFromContext(context).let { realm ->
            val data = realm.where<LooprToken>()
                    .equalTo(LooprToken::ticker, ticker, Case.INSENSITIVE)
                    .findFirst() ?: return null

            return@let realm.copyFromRealm(data)
        }
    }

    fun getAllTokens(context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprToken>> {
        return getRealmFromContext(context)
                .where<LooprToken>()
                .sort(LooprToken::name)
                .findAllAsync()
                .asLiveData()
    }

    /**
     * @param address The address of the wallet whose balances will be retrieved
     */
    fun getAllTokensWithoutZeroBalance(address: String, context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprToken>> {
        return getRealmFromContext(context)
                .where<LooprToken>()
                .equalTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::address, address)
                .notEqualTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::mBalance, "0")
                .sort(LooprToken::name)
                .findAllAsync()
                .asLiveData()
    }

    fun getToken(contractAddress: String, context: HandlerContext = UI): LiveData<LooprToken> {
        return getRealmFromContext(context)
                .where<LooprToken>()
                .equalTo(LooprToken::identifier, contractAddress)
                .findFirstAsync()
                .asLiveData()
    }

}