package org.loopring.looprwallet.core.viewmodels.price

import android.support.test.runner.AndroidJUnit4
import kotlinx.coroutines.experimental.delay
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.sync.SyncData
import java.math.BigDecimal
import java.util.*

/**
 * Created by Corey on 3/22/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class LooprTokenPriceCheckerViewModelTest : BaseDaggerTest() {

    private lateinit var looprTokenPriceCheckerViewModel: LooprTokenPriceCheckerViewModel

    @Before
    fun setup() = runBlockingUiCode {
        looprTokenPriceCheckerViewModel = LooprTokenPriceCheckerViewModel()
    }

    @After
    fun tearDown() = runBlockingUiCode {
        looprTokenPriceCheckerViewModel.clear()
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val liveData = looprTokenPriceCheckerViewModel.getLiveDataFromRepository(LooprToken.LRC.contractAddress)
        val data = liveData.value!!

        assertTrue(data.load())
        assertTrue(looprTokenPriceCheckerViewModel.isDataValid(data))
    }

    @Test
    fun getDataFromNetwork() = runBlockingUiCode {
        val lrc = LooprToken.LRC
        val data = looprTokenPriceCheckerViewModel.getDataFromNetwork(lrc.contractAddress).await()

        assertEquals(lrc.contractAddress, data.identifier)
        assertEquals(lrc.ticker, data.ticker)

        assertNotNull(data.priceInUsd)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val lrc = LooprToken.LRC
        val priceInUsd = BigDecimal("104.25")
        val date = Date()
        val data = LooprToken(lrc.contractAddress, lrc.ticker, priceInUsd = priceInUsd, lastUpdated = date)
        looprTokenPriceCheckerViewModel.addNetworkDataToRepository(data)

        delay(100)

        val insertedData = looprTokenPriceCheckerViewModel.repository.getToken(data.contractAddress).value!!

        assertTrue(insertedData.load())

        // There shouldn't be more than a 10ms difference
        assertDatesWithRange(date, insertedData.lastUpdated, 10)
        assertEquals(priceInUsd, insertedData.priceInUsd)
    }

    @Test
    fun getSyncType() {
        assertEquals(SyncData.SYNC_TYPE_TOKEN_PRICE, looprTokenPriceCheckerViewModel.syncType)
    }

}