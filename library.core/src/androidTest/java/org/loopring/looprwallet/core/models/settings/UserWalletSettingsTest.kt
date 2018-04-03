package org.loopring.looprwallet.core.models.settings

import android.support.test.runner.AndroidJUnit4
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.settings.UserWalletSettings.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.models.settings.UserWalletSettings
import java.util.*
import javax.inject.Inject

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class UserWalletSettingsTest : BaseDaggerTest() {

    @Inject
    lateinit var userWalletSettings: UserWalletSettings

    private val privateKeyOne = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e491"
    private val privateKeyTwo = "e8ef822b865355634d5fc82a693174680acf5cc7beaf19bea33ee62581d8e492"

    @Before
    fun setup() {
        component.inject(this)
    }

    override fun putDefaultWallet() {
        // NO OP; we don't want to put a default wallet; it will break these tests.
        // Instead, we are assuming the wallet is in a "clean state"
    }

    @Test
    fun getLockoutTime() {
        val lockoutTime = userWalletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    @Test
    fun putLockoutTime() {
        val lockoutTime = userWalletSettings.getLockoutTime()

        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)

        userWalletSettings.putLockoutTime(LockoutTimes.FIVE_MINUTES_MILLIS)
        assertEquals(LockoutTimes.FIVE_MINUTES_MILLIS, userWalletSettings.getLockoutTime())

        userWalletSettings.putLockoutTime(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS)
        assertEquals(LockoutTimes.DEFAULT_LOCKOUT_TIME_MILLIS, lockoutTime)
    }

    @Test
    fun getEmptyWallet() {
        assertNull(userWalletSettings.getCurrentWallet())
    }

    @Test
    fun createWallet() {
        // Add the first wallet
        val firstWalletName = "loopr1"
        assertTrue(userWalletSettings.createWallet(firstWalletName, privateKeyOne))

        userWalletSettings.getCurrentWallet()
        val firstWallet = userWalletSettings.getWallet(firstWalletName)

        assertEquals(firstWallet, userWalletSettings.getCurrentWallet())

        val firstRealmKey = userWalletSettings.getRealmKey(firstWalletName)

        assertNotNull(firstRealmKey)
        assertEquals(1, userWalletSettings.getAllWallets().size)

        // Add a second wallet

        val secondWalletName = "loopr2"
        assertTrue(userWalletSettings.createWallet(secondWalletName, privateKeyTwo))

        val secondWallet = userWalletSettings.getWallet(secondWalletName)
        assertEquals(secondWallet, userWalletSettings.getCurrentWallet())

        val secondRealmKey = userWalletSettings.getRealmKey(secondWalletName)

        assertFalse(Arrays.equals(firstRealmKey, secondRealmKey))
        assertEquals(2, userWalletSettings.getAllWallets().size)

        // Remove second wallet and assert that first is now selected

        userWalletSettings.removeWallet(secondWalletName)

        assertEquals(firstWallet, userWalletSettings.getCurrentWallet())
        assertTrue(Arrays.equals(firstRealmKey, userWalletSettings.getRealmKey(firstWalletName)))
        assertEquals(1, userWalletSettings.getAllWallets().size)
    }

}