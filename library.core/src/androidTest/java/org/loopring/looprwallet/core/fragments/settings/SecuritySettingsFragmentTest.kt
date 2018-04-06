package org.loopring.looprwallet.core.fragments.settings

import android.support.v7.preference.ListPreference
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.BaseDaggerFragmentTest
import org.loopring.looprwallet.core.models.settings.LooprSettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.KEY_SECURITY_TYPE
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.TYPE_DEFAULT_VALUE_SECURITY
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
import kotlinx.coroutines.experimental.delay
import org.junit.Assert.*
import org.junit.Test
import org.loopring.looprwallet.core.fragments.security.EnterNewSecurityFragment

/**
 * Created by Corey Caplan on 3/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SecuritySettingsFragmentTest : BaseDaggerFragmentTest<SecuritySettingsFragment>() {

    override val fragment = SecuritySettingsFragment()

    override val tag = SecuritySettingsFragment.TAG

    @Test
    fun onPressChangeLockType() = runBlockingUiCode {
        val key = SecuritySettings.KEY_SECURITY_TYPE
        val value = SecuritySettings.TYPE_PIN_SECURITY

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        delay(300)

        checkCurrentFragmentByContainer(R.id.activityContainer, EnterNewSecurityFragment.TAG)
    }

    @Test
    fun changeSecurityTimeout() = runBlockingUiCode {
        LooprSettings.getInstance(fragment.context!!).putString(KEY_SECURITY_TYPE, TYPE_PIN_SECURITY)

        val key = SecuritySettings.KEY_SECURITY_TIMEOUT
        val value = SecuritySettings.ARRAY_SECURITY_TIMEOUT[0]

        val preference = fragment.findPreference(key) as ListPreference
        preference.value = value

        checkPreferenceKeyAndValue(key, value)
    }

    @Test
    fun onSecurityEnabled() = runBlockingUiCode {
        fragment.onSecurityEnabled(TYPE_PIN_SECURITY)

        val settings = LooprSettings.getInstance(fragment.context!!)
        assertEquals(TYPE_PIN_SECURITY, settings.getString(KEY_SECURITY_TYPE))

        fragment.getPreferenceKeysAndDefaultValues().forEach {
            assertTrue(fragment.findPreference(it.first).isEnabled)
        }
    }

    @Test
    fun onSecurityDisabled() = runBlockingUiCode {
        fragment.onSecurityDisabled()

        val settings = LooprSettings.getInstance(fragment.context!!)
        assertEquals(TYPE_DEFAULT_VALUE_SECURITY, settings.getString(KEY_SECURITY_TYPE))

        fragment.getPreferenceKeysAndDefaultValues().forEach {
            if (it.first != KEY_SECURITY_TYPE) {
                assertFalse(fragment.findPreference(it.first).isEnabled)
            } else {
                assertTrue(fragment.findPreference(it.first).isEnabled)
            }
        }
    }

}