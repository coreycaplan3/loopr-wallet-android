package com.caplaninnovations.looprwallet.models.android.settings

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings.*
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.*

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class WalletSettingsTest : BaseDaggerTest() {

    private val privateKeyOne = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e491"
    private val privateKeyTwo = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e492"

    override fun putDefaultWallet() {
        // NO OP; we don't want to put a default wallet; it will break these tests.
        // Instead, we are assuming the wallet is in a "clean state"
    }

    @Test
    fun getLockoutTime() {
        val lockoutTime = walletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    @Test
    fun putLockoutTime() {
        val lockoutTime = walletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)

        val newLockoutTime = LockoutTimes.FIVE_MINUTES_MILLIS
        walletSettings.putLockoutTime(newLockoutTime)

        assertEquals(newLockoutTime, walletSettings.getLockoutTime())

        assertNotEquals(lockoutTime, newLockoutTime)
    }

    @Test
    fun getEmptyWallet() {
        assertNull(walletSettings.getCurrentWallet())
    }

    @Test
    fun createWallet() {
        // Add the first wallet
        val firstWalletName = "loopr1"
        assertTrue(walletSettings.createWallet(firstWalletName, privateKeyOne))

        walletSettings.getCurrentWallet()
        val firstWallet = walletSettings.getWallet(firstWalletName)

        assertEquals(firstWallet, walletSettings.getCurrentWallet())

        val firstRealmKey = walletSettings.getRealmKey(firstWalletName)

        assertNotNull(firstRealmKey)
        assertEquals(1, walletSettings.getAllWallets().size)

        // Add a second wallet

        val secondWalletName = "loopr2"
        assertTrue(walletSettings.createWallet(secondWalletName, privateKeyTwo))

        val secondWallet = walletSettings.getWallet(secondWalletName)
        assertEquals(secondWallet, walletSettings.getCurrentWallet())

        val secondRealmKey = walletSettings.getRealmKey(secondWalletName)

        assertFalse(Arrays.equals(firstRealmKey, secondRealmKey))
        assertEquals(2, walletSettings.getAllWallets().size)

        // Remove Xander's wallet and assert that Corey is now selected

        walletSettings.removeWallet(secondWalletName)

        assertEquals(firstWallet, walletSettings.getCurrentWallet())
        assertTrue(Arrays.equals(firstRealmKey, walletSettings.getRealmKey(firstWalletName)))
        assertEquals(1, walletSettings.getAllWallets().size)
    }

}