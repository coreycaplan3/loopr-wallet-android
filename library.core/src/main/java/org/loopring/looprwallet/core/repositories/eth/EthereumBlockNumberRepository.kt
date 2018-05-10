package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.repositories.BaseRealmRepository
import org.loopring.looprwalletnetwork.models.ethereum.EthBlockNum

/**
 * Created by Corey on 4/23/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 *
 */
class EthereumBlockNumberRepository : BaseRealmRepository(false) {

    fun getEthereumBlockNumber(context: HandlerContext = UI): LiveData<EthBlockNum> {
        return getRealmFromContext(context)
                .where<EthBlockNum>()
                .findFirstAsync()
                .asLiveData()
    }

}