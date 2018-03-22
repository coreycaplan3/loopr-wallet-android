package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.repositories.BaseRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.util.*

/**
 * Created by Corey on 3/21/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A default instance of [OfflineFirstViewModel] that uses an in-memory
 * repository for storing one instance of a [String] as data.
 */
class OfflineFirstViewModelImplTest : OfflineFirstViewModel<EthToken, String>() {

    companion object {
        val DATA = EthToken.ETH
    }

    override val repository = object : BaseRepository<EthToken> {

        override fun add(data: EthToken) {
            repositoryData = data
        }

        override fun addList(data: List<EthToken>) {
        }

        override fun removeData(data: EthToken) {
        }

        override fun removeData(data: List<EthToken>) {
        }

        override fun clear() {
            repositoryData = null
        }
    }

    var repositoryData: EthToken? = null
        private set

    fun createLiveData(parameter: String, onChange: (EthToken) -> Unit) {
        initializeDataForever(parameter, onChange)
    }

    fun createLiveDataForever(parameter: String, onChange: (EthToken) -> Unit) {
        initializeDataForever(parameter, onChange)
    }

    public override fun getLiveDataFromRepository(parameter: String): LiveData<EthToken> {
        return MutableLiveData<EthToken>()
    }

    public override fun getDataFromNetwork(parameter: String): Deferred<EthToken> = async {
        delay(500L)
        DATA
    }

    public override fun addNetworkDataToRepository(data: EthToken) {
        (mLiveData as MutableLiveData).postValue(data)
    }

    val requiresRefresh = Date().time - (1000L * 40L)
    val noRefresh = Date().time - (1000L)
    var lastRefresh = requiresRefresh

    public override fun isRefreshNecessary(): Boolean {
        return isDefaultRefreshNecessary(lastRefresh, DEFAULT_WAIT_TIME_MILLIS)
    }

    public override fun onLiveDataInitialized(liveData: LiveData<EthToken>) {
        super.onLiveDataInitialized(liveData)
    }

    public override fun isPredicatesEqual(oldParameter: String?, newParameter: String): Boolean {
        return super.isPredicatesEqual(oldParameter, newParameter)
    }

    public override fun isDataValid(data: EthToken?): Boolean {
        return super.isDataValid(data)
    }

    public override fun isDataEmpty(data: EthToken?): Boolean {
        return super.isDataEmpty(data)
    }
}