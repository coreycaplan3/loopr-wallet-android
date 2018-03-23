package com.caplaninnovations.looprwallet.viewmodels

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 3/21/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
open class OfflineFirstViewModelTest : BaseDaggerTest() {

    /**
     * Used for testing [isDataEmpty] and [isDataValid]
     */
    private lateinit var realm: Realm

    private val parameter = "param"

    private lateinit var offlineFirstViewModel: OfflineFirstViewModelImplTest

    @Before
    fun setUp() = runBlockingUiCode {
        realm = Realm.getInstance(RealmConfiguration.Builder().inMemory().build())
        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        offlineFirstViewModel.createLiveData(parameter) { onChange() }
    }

    private fun onChange() {

    }

    @After
    fun tearDown() = runBlockingUiCode {
        realm.close()
        offlineFirstViewModel.clear()
    }

    @Test
    fun checkFullFunctionality_repositoryHasData() = runBlockingUiCode {
        realm.close()
        offlineFirstViewModel.clear()

        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        offlineFirstViewModel.repositoryData = EthToken.ETH
        assertTrue(offlineFirstViewModel.isRefreshNecessary(parameter))
        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)

        val repositoryDeferred = CompletableDeferred<EthToken>()
        val networkDeferred = CompletableDeferred<EthToken>()

        offlineFirstViewModel.createLiveData(parameter, {
            if (offlineFirstViewModel.mIsNetworkOperationRunning) {
                repositoryDeferred.complete(it)
            } else if (!offlineFirstViewModel.isLoading()) {
                networkDeferred.complete(it)
            }
        })

        val repositoryData = withTimeoutOrNull(1000L) { repositoryDeferred.await() }
        assertEquals(EthToken.ETH.identifier, repositoryData!!.identifier)
        assertTrue(repositoryDeferred.isCompleted)
        assertFalse(networkDeferred.isCompleted)

        val networkData = withTimeoutOrNull(1000L) { networkDeferred.await() }
        assertEquals(EthToken.ETH.identifier, networkData!!.identifier)

        assertEquals(OfflineFirstViewModel.STATE_IDLE_HAVE_DATA, offlineFirstViewModel.currentState)
    }

    @Test
    fun checkFullFunctionality_repositoryHasNoData() = runBlockingUiCode {
        realm.close()
        offlineFirstViewModel.clear()

        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        assertTrue(offlineFirstViewModel.isRefreshNecessary(parameter))
        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)

        val repositoryDeferred = CompletableDeferred<EthToken>()
        val networkDeferred = CompletableDeferred<EthToken>()

        offlineFirstViewModel.createLiveData("hello", {
            if (offlineFirstViewModel.isLoading() && offlineFirstViewModel.mIsNetworkOperationRunning) {
                repositoryDeferred.complete(it)
            }
            if (!offlineFirstViewModel.mIsNetworkOperationRunning) {
                networkDeferred.complete(it)
            }
        })

        assertNull(withTimeoutOrNull(1000L) { repositoryDeferred.await() })

        assertEquals(EthToken.ETH.identifier, networkDeferred.await().identifier)

        // The repository deferred should not have been completed, since there was no data in the
        // repository at first.
        assertEquals(OfflineFirstViewModel.STATE_IDLE_HAVE_DATA, offlineFirstViewModel.currentState)
    }

    @Test
    fun checkFullFunctionality_noDataAtAll() = runBlockingUiCode {
        realm.close()
        offlineFirstViewModel.clear()

        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        // We are faking not having data by not needing the network call
        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.noRefresh
        assertFalse(offlineFirstViewModel.isRefreshNecessary(parameter))
        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)

        val repositoryDeferred = CompletableDeferred<EthToken>()
        val networkDeferred = CompletableDeferred<EthToken>()

        offlineFirstViewModel.createLiveData("hello", {
            if (offlineFirstViewModel.mIsNetworkOperationRunning) {
                repositoryDeferred.complete(it)
            }
            if (!offlineFirstViewModel.mIsNetworkOperationRunning) {
                networkDeferred.complete(it)
            }
        })

        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)

        val repositoryData = withTimeoutOrNull(1000L) { repositoryDeferred.await() }
        assertNull(repositoryData)

        val networkData = withTimeoutOrNull(1000L) { networkDeferred.await() }
        assertNull(networkData)

        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)
    }

    @Test
    fun getRepository() {
        offlineFirstViewModel.repository
    }

    @Test
    fun getData() {
        assertNull(offlineFirstViewModel.data)
    }

    @Test
    fun getCurrentState() = runBlockingUiCode {
        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        assertEquals(OfflineFirstViewModel.STATE_IDLE_EMPTY, offlineFirstViewModel.currentState)
    }

    @Test
    fun isIdle() = runBlockingUiCode {
        // We should be loading as soon as we finish the setup method
        assertFalse(offlineFirstViewModel.isIdle())
    }

    @Test
    fun isLoading() = runBlockingUiCode {
        // We should be loading as soon as we finish the setup method
        assertTrue(offlineFirstViewModel.isLoading())
    }

    @Test
    fun hasValidData() = runBlockingUiCode {
        assertFalse(offlineFirstViewModel.hasValidData())

        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.requiresRefresh
        offlineFirstViewModel.refresh()

        offlineFirstViewModel.blockingNetworkObserver()

        assertTrue(offlineFirstViewModel.hasValidData())
    }

    @Test
    fun refresh() = runBlockingUiCode {
        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.requiresRefresh
        offlineFirstViewModel.refresh()
        assertTrue(offlineFirstViewModel.mIsNetworkOperationRunning)

        delay(1000L)
        assertFalse(offlineFirstViewModel.mIsNetworkOperationRunning)

        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.noRefresh
        offlineFirstViewModel.refresh()
        assertFalse(offlineFirstViewModel.mIsNetworkOperationRunning)
    }

    @Test
    fun initializeData() {
        assertTrue(offlineFirstViewModel.mLiveData!!.hasActiveObservers())
    }

    @Test
    fun initializeDataForever() = runBlockingUiCode {
        offlineFirstViewModel.mLiveData!!.removeObserver(offlineFirstViewModel.foreverObserver!!)
        assertFalse(offlineFirstViewModel.mLiveData!!.hasActiveObservers())

        offlineFirstViewModel.createLiveDataForever(parameter) { onChange() }
        assertTrue(offlineFirstViewModel.mLiveData!!.hasActiveObservers())
    }

    @Test
    fun getLiveDataFromRepository() {
        // We already make a call to get "getLiveDataFromRepository" in the setup method
        assertNotNull(offlineFirstViewModel.mLiveData)
    }

    @Test
    fun getDataFromNetwork() = runBlocking {
        val data = offlineFirstViewModel.getDataFromNetwork(parameter).await()
        assertNotNull(data)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val data = EthToken()
        offlineFirstViewModel.addNetworkDataToRepository(data)
        assertEquals(data, offlineFirstViewModel.blockingNetworkObserver())
    }

    @Test
    fun isPredicatesEqual() {
        assertTrue(offlineFirstViewModel.isPredicatesEqual(offlineFirstViewModel.mParameter, parameter))

        assertFalse(offlineFirstViewModel.isPredicatesEqual(offlineFirstViewModel.mParameter, "not-equal-parameter"))
    }

    @Test
    fun isRefreshNecessary() {
        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.noRefresh
        assertFalse(offlineFirstViewModel.isRefreshNecessary(parameter))

        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.requiresRefresh
        assertTrue(offlineFirstViewModel.isRefreshNecessary(parameter))
    }

    @Test
    fun isDataValid() = runBlockingUiCode {
        val token = realm.where<EthToken>().findFirstAsync()
        assertFalse(offlineFirstViewModel.isDataValid(token))
        assertFalse(offlineFirstViewModel.isDataValid(null))

        assertTrue(offlineFirstViewModel.isDataValid(EthToken.ETH))
    }

    @Test
    fun isDataEmpty() = runBlockingUiCode {
        val token = realm.where<EthToken>().findFirstAsync()
        assertTrue(offlineFirstViewModel.isDataEmpty(token))
        assertTrue(offlineFirstViewModel.isDataEmpty(null))

        assertFalse(offlineFirstViewModel.isDataEmpty(EthToken.ETH))
    }

    @Test
    fun clear() = runBlockingUiCode {
        offlineFirstViewModel.clear()
        assertNull(offlineFirstViewModel.repositoryData)
    }

}