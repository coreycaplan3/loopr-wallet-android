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
     * Finds [EthToken.ETH] synchronously. If not found, it is inserted and returned from realm.
     */
    fun getEthNow(): EthToken =
            uiSharedRealm.where<EthToken>()
                    .equalTo(EthToken::ticker, EthToken.ETH.ticker)
                    .findFirst()
                    .mapIfNull { uiSharedRealm.copyToRealm(EthToken.ETH) }

    @Suppress("UNCHECKED_CAST")
    fun getAllTokens(): LiveData<List<CryptoToken>> {
        return uiSharedRealm.where<EthToken>()
                .findAllAsync()
                .asLiveData() as LiveData<List<CryptoToken>>
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
    fun getToken(contractAddress: String): LiveData<CryptoToken> {
        return uiSharedRealm.where<EthToken>()
                .equalTo(EthToken::contractAddress, contractAddress)
                .findFirstAsync()
                .asLiveData() as LiveData<CryptoToken>
    }

}