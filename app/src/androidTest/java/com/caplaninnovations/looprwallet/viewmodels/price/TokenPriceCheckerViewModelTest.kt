package com.caplaninnovations.looprwallet.viewmodels.price

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import com.caplaninnovations.looprwallet.models.crypto.eth.EthToken
import com.caplaninnovations.looprwallet.models.user.SyncData
import io.realm.RealmObject
import io.realm.kotlin.load
import junit.framework.Assert.*
import kotlinx.coroutines.experimental.delay
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*

/**
 * Created by Corey on 3/22/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class TokenPriceCheckerViewModelTest : BaseDaggerTest() {

    private lateinit var tokenPriceCheckerViewModel: TokenPriceCheckerViewModel

    @Before
    fun setup() = runBlockingUiCode {
        tokenPriceCheckerViewModel = TokenPriceCheckerViewModel(wallet!!)
    }

    @After
    fun tearDown() = runBlockingUiCode {
        tokenPriceCheckerViewModel.clear()
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val liveData = tokenPriceCheckerViewModel.getLiveDataFromRepository(EthToken.LRC.contractAddress)
        val data = (liveData.value as RealmObject)

        assertTrue(data.load())
        assertTrue(tokenPriceCheckerViewModel.isDataValid(data as? CryptoToken))
    }

    @Test
    fun getDataFromNetwork() = runBlockingUiCode {
        val lrc = EthToken.LRC
        val data = tokenPriceCheckerViewModel.getDataFromNetwork(lrc.contractAddress).await()

        assertEquals(lrc.contractAddress, data.identifier)
        assertEquals(lrc.ticker, data.ticker)

        assertNotNull(data.priceInUsd)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val lrc = EthToken.LRC
        val priceInUsd = BigDecimal("104.25")
        val date = Date()
        val data = EthToken(lrc.contractAddress, lrc.ticker, priceInUsd = priceInUsd, lastUpdated = date)
        tokenPriceCheckerViewModel.addNetworkDataToRepository(data)

        delay(100)

        val insertedData = tokenPriceCheckerViewModel.repository.getToken(data.contractAddress).value!!

        assertTrue(insertedData.load())

        // There shouldn't be more than a 10ms difference
        assertDatesWithRange(date, insertedData.lastUpdated, 10)
        assertEquals(priceInUsd, insertedData.priceInUsd)
    }

    @Test
    fun getSyncType() {
        assertEquals(SyncData.SYNC_TYPE_TOKEN_PRICE, tokenPriceCheckerViewModel.syncType)
    }

}