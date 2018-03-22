package com.caplaninnovations.looprwallet.viewmodels.wallet

import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.crypto.CryptoToken
import io.realm.RealmResults
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Corey Caplan on 3/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class TokenBalanceViewModelTest : BaseDaggerTest() {

    private lateinit var tokenBalanceViewModel: TokenBalanceViewModel
    private lateinit var address: String

    @Before
    fun setup() {
        tokenBalanceViewModel = TokenBalanceViewModel(wallet!!)
        address = wallet!!.credentials.address
    }

    @After
    fun tearDown() {
        tokenBalanceViewModel.clear()
    }

    @Test
    fun getLiveDataFromRepository() = runBlockingUiCode {
        val data = tokenBalanceViewModel.getLiveDataFromRepository(address)
        val realmData = (data.value!! as RealmResults<CryptoToken>)
        assertFalse(realmData.isValid)
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

        val allTokens = tokenBalanceViewModel.repository.getAllTokens().value!!
        allTokens.forEach {
            assertNotNull(it.priceInUsd)
            assertNotNull(it.balance)
        }
    }

    @Test
    fun isRefreshNecessary() {
        // TODO refresh repository
    }

}