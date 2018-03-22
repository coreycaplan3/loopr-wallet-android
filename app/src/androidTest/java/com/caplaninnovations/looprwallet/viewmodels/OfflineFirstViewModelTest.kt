package com.caplaninnovations.looprwallet.viewmodels

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.activities.TestActivity
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Created by Corey Caplan on 3/21/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
open class OfflineFirstViewModelTest {

    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var realm: Realm

    private val parameter = "param"

    private lateinit var offlineFirstViewModel: OfflineFirstViewModelImplTest

    /**
     * @param block The block to be run on the UI thread.
     */
    private fun runBlockingUiCode(block: suspend () -> Unit) {
        val deferred = CompletableDeferred<Unit>()
        launch(UI) {
            try {
                block()
                deferred.complete(Unit)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }
        val isExceptionThrown = try {
            runBlocking { deferred.await() }
            false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        assertFalse(isExceptionThrown)
    }

    @Before
    fun setUp() = runBlockingUiCode {
        realm = Realm.getInstance(RealmConfiguration.Builder().inMemory().build())
        offlineFirstViewModel = OfflineFirstViewModelImplTest()
        offlineFirstViewModel.createLiveData(parameter) { onChange() }
    }

    private fun onChange() {

    }

    @After
    fun tearDown() {
        runBlockingUiCode {
            realm.close()
            offlineFirstViewModel.clear()
        }
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
    fun isIdle() {
        // We should be loading as soon as we finish the setup method
        assertFalse(offlineFirstViewModel.isIdle())
    }

    @Test
    fun isLoading() {
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
        assertFalse(offlineFirstViewModel.isRefreshNecessary())

        offlineFirstViewModel.lastRefresh = offlineFirstViewModel.requiresRefresh
        assertTrue(offlineFirstViewModel.isRefreshNecessary())
    }

    @Test
    fun isDefaultRefreshNecessary() {
        val lastRefreshNoRefresh = Date().time
        val waitTime = OfflineFirstViewModel.DEFAULT_WAIT_TIME_MILLIS
        assertFalse(offlineFirstViewModel.isDefaultRefreshNecessary(lastRefreshNoRefresh, waitTime))

        val lastRefreshRequireRefresh = Date().time - waitTime - 1000L
        assertTrue(offlineFirstViewModel.isDefaultRefreshNecessary(lastRefreshRequireRefresh, waitTime))
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