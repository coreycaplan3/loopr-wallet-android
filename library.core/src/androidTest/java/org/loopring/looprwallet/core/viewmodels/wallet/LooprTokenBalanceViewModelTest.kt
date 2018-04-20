package org.loopring.looprwallet.core.viewmodels.wallet

import android.support.test.runner.AndroidJUnit4
import io.realm.RealmResults
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.viewmodels.eth.EthTokenBalanceViewModel
import java.util.*

/**
 * Created by Corey Caplan on 3/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class LooprTokenBalanceViewModelTest : BaseDaggerTest() {

    private lateinit var ethTokenBalanceViewModel: EthTokenBalanceViewModel

    @Before
    fun setup() = runBlockingUiCode {
        ethTokenBalanceViewModel = EthTokenBalanceViewModel()
    }

    @After
    fun tearDown() = runBlockingUiCode {
        ethTokenBalanceViewModel.clear()
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val data = ethTokenBalanceViewModel.getLiveDataFromRepository(address)
        val realmData = data.value as RealmResults<LooprToken>

        assertTrue(realmData.load())

        assertTrue(realmData.isValid)
        assertTrue(realmData.size > 0)
    }

    @Test
    fun getDataFromNetwork() = runBlockingUiCode {
        val list = ethTokenBalanceViewModel.getDataFromNetwork(address).await()
        assertEquals(4, list.size)
    }

    @Test
    fun addNetworkDataToRepository() = runBlockingUiCode {
        val list = ethTokenBalanceViewModel.getDataFromNetwork(address).await()
        ethTokenBalanceViewModel.addNetworkDataToRepository(list)

        val allTokens = (ethTokenBalanceViewModel.repository.getAllTokens().value as RealmResults<LooprToken>)
        assertTrue(allTokens.load())
        val date = Date()
        allTokens.forEach {
            assertDatesWithRange(it.lastUpdated, date, 10)
            assertNotNull(it.priceInUsd)
            assertNotNull(it.tokenBalances.find { it.address == address })
        }
    }

}