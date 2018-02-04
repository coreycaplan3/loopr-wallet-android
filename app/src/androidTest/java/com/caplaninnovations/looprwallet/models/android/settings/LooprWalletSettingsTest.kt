package com.caplaninnovations.looprwallet.models.android.settings

import com.caplaninnovations.looprwallet.dagger.BaseDaggerTest
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings.*
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class LooprWalletSettingsTest: BaseDaggerTest() {

    private lateinit var looprWalletSettings: LooprWalletSettings

    @Before
    fun setUp() {
        looprWalletSettings = LooprWalletSettings(looprSettings)
    }

    override fun putDefaultWallet() {
        // NO OP; we don't want to put a default wallet; it will break these tests...
    }

    @Test
    fun getLockoutTime() {
        val lockoutTime = looprWalletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    @Test
    fun putLockoutTime() {
        val lockoutTime = looprWalletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)

        val newLockoutTime = LockoutTimes.FIVE_MINUTES_MILLIS
        looprWalletSettings.putLockoutTime(newLockoutTime)

        assertEquals(newLockoutTime, looprWalletSettings.getLockoutTime())

        assertNotEquals(lockoutTime, newLockoutTime)
    }

    @Test
    fun getEmptyWallet() {
        assertNull(looprWalletSettings.getCurrentWallet())
    }

    @Test
    fun createWallet() {
        // Add the first wallet
        val firstWalletName = "corey"
        looprWalletSettings.createWallet(firstWalletName)

        assertEquals(firstWalletName, looprWalletSettings.getCurrentWallet())

        val firstRealmKey = looprWalletSettings.getRealmKey(firstWalletName)
        assertNotNull(firstRealmKey)

        assertEquals(1, looprWalletSettings.getAllWallets().size)

        // Add a second wallet

        val secondWalletName = "alexander"
        looprWalletSettings.createWallet(secondWalletName)

        assertEquals(secondWalletName, looprWalletSettings.getCurrentWallet())

        val secondRealmKey = looprWalletSettings.getRealmKey(secondWalletName)

        assertFalse(Arrays.equals(firstRealmKey, secondRealmKey))

        assertEquals(2, looprWalletSettings.getAllWallets().size)

        // Remove Xander's wallet and assert that Corey is now selected

        looprWalletSettings.removeWallet(secondWalletName)

        assertEquals(firstWalletName, looprWalletSettings.getCurrentWallet())

        assertTrue(Arrays.equals(firstRealmKey, looprWalletSettings.getRealmKey(firstWalletName)))

        assertEquals(1, looprWalletSettings.getAllWallets().size)
    }

}