package com.caplaninnovations.looprwallet.realm

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.removeAllListenersAndClose
import org.loopring.looprwallet.core.cryptotokens.EthToken
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey on 3/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
@RunWith(AndroidJUnit4::class)
class RealmObjectChangeListenerTest : BaseDaggerTest() {

    private lateinit var realm: Realm

    @Before
    fun setup() = runBlockingUiCode {
        realm = component.realmClient.getPrivateInstance(wallet!!.walletName, wallet!!.realmKey)
    }

    @After
    fun tearDown() = runBlockingUiCode {
        realm.removeAllListenersAndClose()
    }

    @Test
    fun checkOnChange_noResultsFound() = runBlockingUiCode {
        val tokenAsync = realm.where<EthToken>()
                .equalTo(EthToken::ticker, "does not exist")
                .findFirstAsync()

        val deferred = CompletableDeferred<EthToken>()
        tokenAsync.addChangeListener { token: EthToken ->
            deferred.complete(token)
        }

        withTimeout(1000L) {
            val token = deferred.await()
            assertNotNull(token)
            assertFalse(token.isValid)
            assertTrue(token.isLoaded)
        }
    }

}