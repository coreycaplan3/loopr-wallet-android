package org.loopring.looprwallet.core.viewmodels.price

import android.support.test.runner.AndroidJUnit4
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.sync.SyncData
import kotlinx.coroutines.experimental.delay
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.viewmodels.price.CurrencyExchangeRateViewModel
import java.util.*

/**
 * Created by Corey on 3/21/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class CurrencyExchangeRateViewModelTest : BaseDaggerTest() {

    private lateinit var currencyExchangeRateViewModel: CurrencyExchangeRateViewModel

    @Before
    fun setup() = runBlockingUiCode {
        currencyExchangeRateViewModel = CurrencyExchangeRateViewModel(wallet!!)
    }

    @After
    fun tearDown() = runBlockingUiCode {
        currencyExchangeRateViewModel.clear()
    }

    @Test
    fun getWaitTime() {
        assertEquals(CurrencyExchangeRateViewModel.TEN_MINUTES_MILLIS, currencyExchangeRateViewModel.waitTime)
    }

    @Test
    fun start() = runBlockingUiCode {
        currencyExchangeRateViewModel.start { }

        val currencyExchangeRate = currencyExchangeRateViewModel.mLiveData!!.value!!
        assertTrue(currencyExchangeRate.load())

        assertTrue(currencyExchangeRateViewModel.isDataValid(currencyExchangeRate))
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val liveData = currencyExchangeRateViewModel.getLiveDataFromRepository(CurrencyExchangeRate.USD.currency)
        assertNotNull(liveData.value)
        assertTrue(liveData.value!!.load())
        assertTrue(currencyExchangeRateViewModel.isDataValid(liveData.value))
    }

    @Test
    fun getDataFromNetwork() = runBlockingUiCode {
        val data = currencyExchangeRateViewModel.getDataFromNetwork(CurrencyExchangeRate.USD.currency).await()
        assertNotNull(data)

        assertEquals(CurrencyExchangeRate.USD.currency, data.currency)
        assertEquals(CurrencyExchangeRate.USD.rateAgainstToUsd, data.rateAgainstToUsd)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val usd = CurrencyExchangeRate.USD
        val date = Date()
        val data = CurrencyExchangeRate(usd.currency, usd.rateAgainstToUsd, date)

        currencyExchangeRateViewModel.addNetworkDataToRepository(data)

        delay(100)

        val liveData = currencyExchangeRateViewModel.repository.getCurrencyExchangeRate(usd.currency)
        val insertedData = liveData.value!!

        assertTrue(insertedData.load())

        assertDatesWithRange(date, insertedData.lastUpdated, 10)
    }

    @Test
    fun getSyncType() {
        assertEquals(SyncData.SYNC_TYPE_CURRENCY_EXCHANGE_RATE, currencyExchangeRateViewModel.syncType)
    }

}