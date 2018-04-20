package org.loopring.looprwallet.core.realm

import android.support.test.runner.AndroidJUnit4
import io.realm.Realm
import io.realm.kotlin.where
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.removeAllListenersAndClose
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken

/**
 * Created by Corey Caplan on 4/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: This class confirms the functionality of [Realm]'s link query chaining. Reason
 * being, it's unclear whether or not chained queries change the underlying preceding queries.
 */
@RunWith(AndroidJUnit4::class)
class RealmQueryChainingTest : BaseDaggerTest() {

    private lateinit var realm: Realm

    @Before
    fun setup() = runBlockingUiCode {
        println("FIRST")
        realm = realmClient.getSharedInstance()
    }

    @After
    fun tearDown() = runBlockingUiCode {
        println("SECOND")
        realm.removeAllListenersAndClose()
    }

    @Test
    fun realmQueryChaining() = runBlockingUiCode {
        val originalTokenList = realm.where<LooprToken>().findAll()

        val originalSize = originalTokenList.size
        assertTrue(originalSize > 0)

        val newTokenList = originalTokenList.where()
                .equalTo(LooprToken::contractAddress, LooprToken.ETH.contractAddress)
                .findAll()

        assertEquals(1, newTokenList.size)
        assertNotEquals(originalSize, newTokenList.size)
        assertEquals(originalSize, originalTokenList.size)
    }

}