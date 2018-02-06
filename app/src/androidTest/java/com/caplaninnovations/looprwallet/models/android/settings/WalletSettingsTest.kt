package com.caplaninnovations.looprwallet.models.android.settings

import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.android.settings.WalletSettings.*
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
        val firstWalletName = "corey"
        walletSettings.createWallet(firstWalletName)

        assertEquals(firstWalletName, walletSettings.getCurrentWallet())

        val firstRealmKey = walletSettings.getRealmKey(firstWalletName)

        assertNotNull(firstRealmKey)
        assertEquals(1, walletSettings.getAllWallets().size)

        // Add a second wallet

        val secondWalletName = "alexander"
        walletSettings.createWallet(secondWalletName)

        assertEquals(secondWalletName, walletSettings.getCurrentWallet())

        val secondRealmKey = walletSettings.getRealmKey(secondWalletName)

        assertFalse(Arrays.equals(firstRealmKey, secondRealmKey))
        assertEquals(2, walletSettings.getAllWallets().size)

        // Remove Xander's wallet and assert that Corey is now selected

        walletSettings.removeWallet(secondWalletName)

        assertEquals(firstWalletName, walletSettings.getCurrentWallet())
        assertTrue(Arrays.equals(firstRealmKey, walletSettings.getRealmKey(firstWalletName)))
        assertEquals(1, walletSettings.getAllWallets().size)
    }

}