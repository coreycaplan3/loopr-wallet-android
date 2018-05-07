package org.loopring.looprwallet.core.repositories.eth

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
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
class EthereumBlockNumberRepository : BaseRealmRepository(false) {

    fun getEthereumBlockNumber(context: HandlerContext = UI): LiveData<EthereumBlockNumber> {
        return getRealmFromContext(context)
                .where<EthereumBlockNumber>()
                .findFirstAsync()
                .asLiveData()
    }

}