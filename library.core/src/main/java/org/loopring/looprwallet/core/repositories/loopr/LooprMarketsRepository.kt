package org.loopring.looprwallet.core.repositories.loopr

import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class LooprMarketsRepository(currentWallet: LooprWallet): BaseRealmRepository(currentWallet) {

    fun getAllMarkets() {
        TODO("TODO")
    }

    fun getFavoritesMarkets() {
        TODO("TODO")
    }

    fun getMarketDetails(tradePair: Any) {
        TODO("TODO")
    }

}