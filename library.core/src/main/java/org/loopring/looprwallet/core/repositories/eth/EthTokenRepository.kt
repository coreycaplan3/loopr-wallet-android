package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.kotlin.where
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
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
class EthTokenRepository : BaseRealmRepository() {

    override fun getRealm() = realmClient.getSharedInstance()

    /**
     * Finds [EthToken.ETH] synchronously. If not found, it is inserted and returned from realm.
     *
     * @return An **UN-MANAGED** [EthToken] that represents ETH.
     */
    fun getEthNow(): EthToken = getRealm().use {
        it.where<EthToken>()
                .equalTo(EthToken::ticker, EthToken.ETH.ticker)
                .findFirst()
                .mapIfNull {
                    val data = uiRealm.copyToRealm(EthToken.ETH)
                    return@mapIfNull uiRealm.copyFromRealm(data)
                }
    }

    /**
     * @return An **UN-MANAGED** [EthToken] that represents the token with the provided contract
     * address.
     */
    fun getTokenByContractAddressNow(contractAddress: String): EthToken? = getRealm().use {
        val data = it.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .findFirst() ?: return null

        return@use it.copyFromRealm(data)
    }

    /**
     * @return An **UN-MANAGED** [EthToken] that represents the token with the provided contract
     * address and wallet address as the balance owner.
     */
    fun getTokenByContractAddressAndAddressNow(contractAddress: String, walletAddress: String): EthToken? = getRealm().use {
        val data = it.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .equalTo(listOf(EthToken::tokenBalances), TokenBalanceInfo::address, walletAddress)
                .findFirst() ?: return null

        return@use it.copyFromRealm(data)
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllTokens(): LiveData<List<EthToken>> {
        return uiRealm.where<EthToken>()
                .findAllAsync()
                .asLiveData() as LiveData<List<EthToken>>
    }

    /**
     * @param address The address of the wallet whose addres s
     */
    @Suppress("UNCHECKED_CAST")
    fun getAllTokensWithoutZeroBalance(address: String): LiveData<List<CryptoToken>> {
        return uiRealm.where<EthToken>()
                .notEqualTo(listOf(EthToken::tokenBalances), TokenBalanceInfo::mBalance, "0")
                .findAllAsync()
                .asLiveData() as LiveData<List<CryptoToken>>
    }

    @Suppress("UNCHECKED_CAST")
    fun getToken(contractAddress: String): LiveData<EthToken> {
        return uiRealm.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .findFirstAsync()
                .asLiveData()
    }

}