package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import org.loopring.looprwallet.core.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.cryptotokens.EthToken
import io.realm.Realm
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.mapIfNull
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

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