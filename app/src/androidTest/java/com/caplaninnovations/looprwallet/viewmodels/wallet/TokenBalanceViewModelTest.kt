package com.caplaninnovations.looprwallet.viewmodels.wallet

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import io.realm.RealmResults
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Created by Corey Caplan on 3/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class TokenBalanceViewModelTest : BaseDaggerTest() {

    private lateinit var tokenBalanceViewModel: TokenBalanceViewModel
    private lateinit var address: String

    @Before
    fun setup() = runBlockingUiCode {
        tokenBalanceViewModel = TokenBalanceViewModel(wallet!!)
        address = wallet!!.credentials.address
    }

    @After
    fun tearDown() = runBlockingUiCode {
        tokenBalanceViewModel.clear()
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val data = tokenBalanceViewModel.getLiveDataFromRepository(address)
        val realmData = (data.value!! as RealmResults<CryptoToken>)

        assertTrue(realmData.load())

        assertTrue(realmData.isValid)
        assertTrue(realmData.size > 0)
    }

    @Test
    fun getDataFromNetwork() = runBlockingUiCode {
        val list = tokenBalanceViewModel.getDataFromNetwork(address).await()
        assertEquals(4, list.size)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val list = tokenBalanceViewModel.getDataFromNetwork(address).await()
        tokenBalanceViewModel.addNetworkDataToRepository(list)

        val allTokens = (tokenBalanceViewModel.repository.getAllTokens().value as RealmResults<CryptoToken>)
        assertTrue(allTokens.load())
        val date = Date()
        allTokens.forEach {
            assertDatesWithRange(it.lastUpdated, date, 10)
            assertNotNull(it.priceInUsd)
            assertNotNull(it.balance)
        }
    }

}