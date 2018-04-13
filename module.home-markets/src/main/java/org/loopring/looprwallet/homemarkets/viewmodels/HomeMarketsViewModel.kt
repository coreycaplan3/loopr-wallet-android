package org.loopring.looprwallet.homemarkets.viewmodels

import android.arch.lifecycle.LiveData
import io.realm.RealmModel
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.markets.MarketsFilter
import org.loopring.looprwallet.core.repositories.BaseRepository
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel

/**
 * Created by Corey Caplan on 4/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class HomeMarketsViewModel: OfflineFirstViewModel<RealmModel, MarketsFilter>() {

    override val repository: BaseRepository<*>
        get() = TODO("not implemented") // TODO

    override fun getLiveDataFromRepository(parameter: MarketsFilter): LiveData<RealmModel> {
        TODO("not implemented") // TODO
    }

    override fun getDataFromNetwork(parameter: MarketsFilter): Deferred<RealmModel> {
        TODO("not implemented") // TODO
    }

    override fun addNetworkDataToRepository(data: RealmModel) {
        TODO("not implemented") // TODO
    }

    override fun isRefreshNecessary(parameter: MarketsFilter): Boolean {
        TODO("not implemented") // TODO
    }

    override fun addSyncDataToRepository(parameter: MarketsFilter) {
        TODO("not implemented") // TODO
    }

    override val syncType: String
        get() = TODO("not implemented") // TODO
}