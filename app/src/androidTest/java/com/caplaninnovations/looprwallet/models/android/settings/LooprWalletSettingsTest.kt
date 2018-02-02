package com.caplaninnovations.looprwallet.models.android.settings

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettingsManager.Keys.KEY_SHARED_PREFERENCE_NAME
import com.caplaninnovations.looprwallet.models.android.settings.LooprWalletSettings.*
import org.junit.After
import org.junit.Before
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
@SmallTest
class LooprWalletSettingsTest {

    private lateinit var context: Context

    private lateinit var looprWalletSettings: LooprWalletSettings

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()

        looprWalletSettings = LooprWalletSettings(context)
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

        val secondWalletName = "xander"
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

    @After
    fun tearDown() {
        context.getSharedPreferences(KEY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
    }

}