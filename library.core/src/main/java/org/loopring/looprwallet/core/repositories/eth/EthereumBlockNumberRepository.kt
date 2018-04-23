package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.models.blockchain.EthereumBlockNumber
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 *
 */
class EthereumBlockNumberRepository : BaseRealmRepository() {

    override fun getRealm(): Realm = realmClient.getSharedInstance()

    fun getEthereumBlockNumber(): LiveData<EthereumBlockNumber> {
        return uiRealm.where<EthereumBlockNumber>()
                .findFirstAsync()
                .asLiveData()
    }

}