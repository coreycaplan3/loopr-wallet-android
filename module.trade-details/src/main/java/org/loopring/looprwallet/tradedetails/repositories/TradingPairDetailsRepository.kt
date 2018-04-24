package org.loopring.looprwallet.tradedetails.repositories

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/24/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class TradingPairDetailsRepository : BaseRealmRepository() {

    override fun getRealm(): Realm = realmClient.getSharedInstance()

    fun getTradingPairByMarket(primaryTicker: String, secondaryTicker: String): LiveData<TradingPair> {
        return uiRealm.where<TradingPair>()
                .equalTo(TradingPair::primaryTicker, primaryTicker)
                .equalTo(TradingPair::secondaryTicker, secondaryTicker)
                .findFirstAsync()
                .asLiveData()
    }

}