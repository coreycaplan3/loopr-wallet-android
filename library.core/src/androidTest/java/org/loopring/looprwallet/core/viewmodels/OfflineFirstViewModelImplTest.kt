package org.loopring.looprwallet.core.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.repositories.BaseRepository
import org.loopring.looprwallet.core.repositories.sync.SyncRepository
import io.realm.RealmModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.models.sync.SyncData
import java.util.*

/**
 * Created by Corey on 3/21/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A default instance of [OfflineFirstViewModel] that uses a [String] instance as
 * a parameter and an  an in-memory repository for storing one instance of an [LooprToken] as data.
 */
class OfflineFirstViewModelImplTest : OfflineFirstViewModel<LooprToken, String>() {

    companion object {
        val DATA = LooprToken.ETH
    }

    // TODO hook into repository GET query
    var repositoryData: LooprToken? = null

    override val repository = object : BaseRepository<LooprToken> {

        override fun add(data: LooprToken) {
            repositoryData = data
        }

        override fun addList(data: List<LooprToken>) {
        }

        override fun remove(data: LooprToken) {
        }

        override fun remove(data: List<LooprToken>) {
        }

        override fun clear() {
            repositoryData = null
        }
    }

    val requiresRefresh = Date(Date().time - (1000L * 40L))
    val noRefresh = Date(Date().time - (1000L))

    var lastRefresh: Date? = null

    override val syncRepository = object : SyncRepository {
        override fun add(data: RealmModel) {
            lastRefresh = Date()
        }

        override fun addList(data: List<RealmModel>) {
        }

        override fun remove(data: RealmModel) {
        }

        override fun getLastSyncTime(syncType: String): Date? {
            return lastRefresh
        }

        override fun getLastSyncTimeForSyncId(syncType: String, syncId: String): Date? {
            return lastRefresh
        }

        override fun remove(data: List<RealmModel>) {
        }

        override fun clear() {
            lastRefresh = null
        }
    }

    override val syncType: String = "TEST"

    fun createLiveData(parameter: String, onChange: (LooprToken) -> Unit) {
        initializeDataForever(parameter, onChange)
    }

    fun createLiveDataForever(parameter: String, onChange: (LooprToken) -> Unit) {
        initializeDataForever(parameter, onChange)
    }

    override fun getLiveDataFromRepository(parameter: String): LiveData<LooprToken> {
        return MutableLiveData<LooprToken>().apply {
            value = repositoryData
        }
    }

    override fun isRefreshNecessary(parameter: String) = defaultIsRefreshNecessary(parameter)

    override fun addSyncDataToRepository(parameter: String) {
        syncRepository.add(SyncData())
    }

    override fun getDataFromNetwork(parameter: String): Deferred<LooprToken> = async(CommonPool) {
        delay(500L)
        DATA
    }

    override fun addNetworkDataToRepository(data: LooprToken) {
        (mLiveData as MutableLiveData).postValue(data)
    }

    public override fun isPredicatesEqual(oldParameter: String?, newParameter: String): Boolean {
        return super.isPredicatesEqual(oldParameter, newParameter)
    }

    public override fun isDataValid(data: LooprToken?): Boolean {
        return super.isDataValid(data)
    }

    public override fun isDataEmpty(data: LooprToken?): Boolean {
        return super.isDataEmpty(data)
    }
}