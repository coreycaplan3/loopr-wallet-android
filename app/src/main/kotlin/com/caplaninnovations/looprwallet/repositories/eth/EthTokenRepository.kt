package com.caplaninnovations.looprwallet.repositories.eth

import android.arch.lifecycle.LiveData
import com.caplaninnovations.looprwallet.extensions.asLiveData
import com.caplaninnovations.looprwallet.extensions.equalTo
import com.caplaninnovations.looprwallet.extensions.mapIfNull
import com.caplaninnovations.looprwallet.extensions.notEqualTo
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.crypto.TokenBalanceInfo
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import io.realm.Realm
import io.realm.kotlin.where

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EthTokenRepository(currentWallet: LooprWallet) : BaseRealmRepository(currentWallet) {

    /**
     * Finds [EthToken.ETH] synchronously. If not found, it is inserted and
     * returned from realm.
     *
     * @param realm The realm to use to run this query
     */
    fun getEthNow(realm: Realm = uiSharedRealm): EthToken =
            realm.where<EthToken>()
                    .equalTo(EthToken::ticker, EthToken.ETH.ticker)
                    .findFirst()
                    .mapIfNull { realm.copyToRealm(EthToken.ETH) }

    fun getTokenByContractAddressNow(contractAddress: String, realm: Realm = uiSharedRealm) =
            realm.where<EthToken>()
                    .equalTo(EthToken::contractAddress, contractAddress)
                    .findFirst()

    fun getTokenByContractAddressAndAddressNow(
            contractAddress: String,
            walletAddress: String,
            realm: Realm = uiSharedRealm
    ): EthToken? {
        return realm.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .equalTo(listOf(EthToken::tokenBalances), TokenBalanceInfo::address, walletAddress)
                .findFirst()
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllTokens(): LiveData<List<EthToken>> {
        return uiSharedRealm.where<EthToken>()
                .findAllAsync()
                .asLiveData() as LiveData<List<EthToken>>
    }

    /**
     * @param address The address of the wallet whose addres s
     */
    @Suppress("UNCHECKED_CAST")
    fun getAllTokensWithoutZeroBalance(address: String): LiveData<List<CryptoToken>> {
        return uiSharedRealm.where<EthToken>()
                .notEqualTo(listOf(EthToken::tokenBalances), TokenBalanceInfo::mBalance, "0")
                .findAllAsync()
                .asLiveData() as LiveData<List<CryptoToken>>
    }

    @Suppress("UNCHECKED_CAST")
    fun getToken(contractAddress: String): LiveData<EthToken> {
        return uiSharedRealm.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .findFirstAsync()
                .asLiveData()
    }

}