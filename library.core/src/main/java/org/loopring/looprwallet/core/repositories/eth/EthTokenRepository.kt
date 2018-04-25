package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.OrderedRealmCollection
import io.realm.kotlin.where
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.models.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.mapIfNull
import org.loopring.looprwallet.core.extensions.notEqualTo
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

    override fun getAsyncRealm() = realmClient.getSharedInstance()

    /**
     * Finds [LooprToken.ETH] synchronously. If not found, it is inserted and returned from realm.
     *
     * @return An **UN-MANAGED** [LooprToken] that represents ETH.
     */
    fun getEthNow(): LooprToken = getAsyncRealm().use {
        it.where<LooprToken>()
                .equalTo(LooprToken::ticker, LooprToken.ETH.ticker)
                .findFirst()
                .mapIfNull {
                    val data = uiRealm.copyToRealm(LooprToken.ETH)
                    return@mapIfNull uiRealm.copyFromRealm(data)
                }
    }

    /**
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided contract
     * address.
     */
    fun getTokenByContractAddressNow(contractAddress: String): LooprToken? = getAsyncRealm().use {
        val data = it.where<LooprToken>()
                .equalTo(LooprToken::identifier, contractAddress)
                .findFirst() ?: return null

        return@use it.copyFromRealm(data)
    }

    /**
     * @return An **UN-MANAGED** [LooprToken] that represents the token with the provided contract
     * address and wallet address as the balance owner.
     */
    fun getTokenByContractAddressAndAddressNow(contractAddress: String, walletAddress: String): LooprToken? = getAsyncRealm().use {
        val data = it.where<LooprToken>()
                .equalTo(LooprToken::identifier, contractAddress)
                .equalTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::address, walletAddress)
                .findFirst() ?: return null

        return@use it.copyFromRealm(data)
    }

    fun getAllTokens(): LiveData<OrderedRealmCollection<LooprToken>> {
        return uiRealm.where<LooprToken>()
                .findAllAsync()
                .asLiveData()
    }

    /**
     * @param address The address of the wallet whose balances will be retrieved
     */
    fun getAllTokensWithoutZeroBalance(address: String): LiveData<OrderedRealmCollection<LooprToken>> {
        return uiRealm.where<LooprToken>()
                .notEqualTo(listOf(LooprToken::tokenBalances), TokenBalanceInfo::mBalance, "0")
                .findAllAsync()
                .asLiveData()
    }

    fun getToken(contractAddress: String): LiveData<LooprToken> {
        return uiRealm.where<LooprToken>()
                .equalTo(LooprToken::identifier, contractAddress)
                .findFirstAsync()
                .asLiveData()
    }

}