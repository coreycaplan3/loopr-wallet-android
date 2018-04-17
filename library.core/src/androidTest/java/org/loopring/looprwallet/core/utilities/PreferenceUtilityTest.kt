package org.loopring.looprwallet.core.utilities

import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.dagger.BaseDaggerTest
import org.loopring.looprwallet.core.models.settings.*

/**
 * Created by Corey Caplan on 3/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
@RunWith(AndroidJUnit4::class)
class PreferenceUtilityTest : BaseDaggerTest() {

    @Test
    fun setDefaultValues() {
        PreferenceUtility.setDefaultValues()

        val settings = LooprSettings.getInstance(instrumentation.targetContext)

        // Test it by getting one value from each xml resource (or screen)
        assertNotNull(settings.getString(ThemeSettings.KEY_THEME))

        assertNotNull(settings.getString(SecuritySettings.KEY_SECURITY_TYPE))

        assertNotNull(settings.getString(EthereumFeeSettings.KEY_TRANSFER_ETHER_GAS_LIMIT))
        assertNotNull(settings.getString(LoopringFeeSettings.KEY_LRC_FEE))

        // Use a default value of TRUE to see if it was indeed set before (since the default value
        // for the preference is FALSE)
        assertFalse(settings.getBoolean(GeneralWalletSettings.KEY_SHOW_ZERO_BALANCES, true))
        assertNotNull(settings.getString(CurrencySettings.KEY_CURRENT_CURRENCY))

        assertNotNull(settings.getString(EthereumNetworkSettings.KEY_NODE))
        assertNotNull(settings.getString(LoopringNetworkSettings.KEY_CONTRACT_VERSION))
    }
}