package org.loopring.looprwallet.core.realm

import android.support.test.runner.AndroidJUnit4
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.extensions.equalTo
import org.loopring.looprwallet.core.extensions.removeAllListenersAndClose
import org.loopring.looprwallet.core.extensions.upsert
import org.loopring.looprwallet.core.models.cryptotokens.TokenBalanceInfo
import org.loopring.looprwallet.core.models.cryptotokens.EthToken
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: This class confirms the functionality of [Realm] when updating a field that
 * contains a list. Reason being, we need to ensure that the list is always added to and NOT
 * replaced when updating the field.
 */
@RunWith(AndroidJUnit4::class)
class RealmOneToManyTest : BaseDaggerTest() {

    private lateinit var realm: Realm

    @Before
    fun setup() = runBlockingUiCode {
        println("FIRST")
        realm = realmClient.getPrivateInstance(wallet!!.walletName, wallet!!.realmKey)
    }

    @After
    fun tearDown() = runBlockingUiCode {
        println("SECOND")
        realm.removeAllListenersAndClose()
    }

    @Test
    fun addOneItemToList_doesNotOverwriteOtherItems() = runBlockingUiCode {
        realm.executeTransaction {
            val balances = RealmList<TokenBalanceInfo>()
            balances.add(TokenBalanceInfo("0x0123456012345601234560123456012345601234"))
            it.upsert(EthToken(tokenBalances = balances))
        }

        var token = realm.where<EthToken>()
                .equalTo(EthToken::contractAddress, "ETH")
                .findFirst()!!
        assertEquals(1, token.tokenBalances.size)

        realm.executeTransaction {
            val balances = RealmList<TokenBalanceInfo>()
            balances.add(TokenBalanceInfo("0xabcdefabcdefabcdefabcdefabcdefabcdefabcd"))

            val eth = realm.where<EthToken>().equalTo(EthToken::contractAddress, "ETH").findFirst()
            eth!!.tokenBalances.addAll(balances)
            realm.upsert(eth)
        }

        token = realm.where<EthToken>()
                .equalTo(EthToken::contractAddress, "ETH")
                .findFirst()!!
        assertEquals(2, token.tokenBalances.size)
    }

}