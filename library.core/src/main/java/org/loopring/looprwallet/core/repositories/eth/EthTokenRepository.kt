package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.notEqualTo
import org.loopring.looprwallet.core.models.android.architecture.IO
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthTokenRepository : BaseRealmRepository(false) {

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
            token != null -> uiRealm.copyFromRealm(token)
            else -> LooprToken.ETH
        }
    }

    /**
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided contract
     * address.
     */
    fun getTokenByContractAddress(contractAddress: String, context: HandlerContext = IO): LooprToken? {
        return getRealmFromContext(context).let {
            val data = it.where<LooprToken>()
                    .equalTo(LooprToken::identifier, contractAddress)
                    .findFirst() ?: return null

            return@let it.copyFromRealm(data)
        }
    }

    /**
     * ** This function must be called from the IO thread **
     *
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided contract
     * address and wallet address as the balance owner.
     */
    fun getTokenByContractAddressAndAddressNow(contractAddress: String, walletAddress: String, context: HandlerContext = UI): LooprToken? {
        return getRealmFromContext(context).let {
            val data = it.where<LooprToken>()
                    .equalTo(LooprToken::identifier, contractAddress)
                    .equalTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::address, walletAddress)
                    .findFirst() ?: return null

            return@let it.copyFromRealm(data)
        }
    }

    fun getAllTokens(context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprToken>> {
        return getRealmFromContext(context)
                .where<LooprToken>()
                .findAllAsync()
                .asLiveData()
    }

    /**
     * @param address The address of the wallet whose balances will be retrieved
     */
    fun getAllTokensWithoutZeroBalance(address: String, context: HandlerContext = UI): LiveData<OrderedRealmCollection<LooprToken>> {
        return getRealmFromContext(context)
                .where<LooprToken>()
                .notEqualTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::mBalance, "0")
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