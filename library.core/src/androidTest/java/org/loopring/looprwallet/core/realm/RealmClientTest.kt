package org.loopring.looprwallet.core.realm

import android.support.test.runner.AndroidJUnit4
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import io.realm.Realm
import io.realm.kotlin.where
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 3/22/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class RealmClientTest {

    private val realmName = "loopr-test"
    private val realmClient = RealmClient.getPrivateInstance()

    private lateinit var realm: Realm

    @Before
    fun setup() {
        realm = Realm.getInstance(realmClient.getPrivateRealmConfigurationBuilder(realmName).build())
    }

    @Test
    fun checkInitialData() {
        val ethTokenList = realm.where<LooprToken>().findAll()
        assertEquals(4, ethTokenList.size)

        val currencyList = realm.where<CurrencyExchangeRate>().findAll()
        assertEquals(1, currencyList.size)
    }

}