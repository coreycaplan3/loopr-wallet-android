package org.loopring.looprwallet.core.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.models.settings.ThemeSettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import javax.inject.Inject

/**
 * Created by Corey on 3/23/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class HomeSettingsFragment : BaseSettingsFragment() {

    companion object {
        val TAG: String = HomeSettingsFragment::class.java.simpleName

        // Screen Keys
        val SCREEN_KEY_SECURITY = ApplicationUtility.str(R.string.settings_security_screen_key)
        val SCREEN_KEY_ETHEREUM_FEES = ApplicationUtility.str(R.string.settings_ethereum_fees_screen_key)
        val SCREEN_KEY_LOOPRING_FEES = ApplicationUtility.str(R.string.settings_loopring_fees_screen_key)
        val SCREEN_KEY_GENERAL_WALLET = ApplicationUtility.str(R.string.settings_general_wallet_screen_key)
        val SCREEN_KEY_CURRENCY = ApplicationUtility.str(R.string.settings_currency_screen_key)
        val SCREEN_KEY_ETHEREUM_NETWORK = ApplicationUtility.str(R.string.settings_ethereum_network_screen_key)
        val SCREEN_KEY_LOOPRING_NETWORK = ApplicationUtility.str(R.string.settings_loopring_network_screen_key)

        val SUMMARY_VALUE_SECURITY_SCREEN = ApplicationUtility.str(R.string.summary_security_screen)
        val SUMMARY_VALUE_ETHEREUM_FEES_SCREEN = ApplicationUtility.str(R.string.summary_ethereum_fees_screen)
        val SUMMARY_VALUE_LOOPRING_FEES_SCREEN = ApplicationUtility.str(R.string.summary_loopring_trading_fees)
        val SUMMARY_VALUE_GENERAL_WALLET_SCREEN = ApplicationUtility.str(R.string.summary_general_wallet_settings)
        val SUMMARY_VALUE_CURRENCY_SCREEN = ApplicationUtility.str(R.string.summary_currency_settings)
        val SUMMARY_VALUE_ETHEREUM_NETWORK_SCREEN = ApplicationUtility.str(R.string.summary_ethereum_network)
        val SUMMARY_VALUE_LOOPRING_NETWORK_SCREEN = ApplicationUtility.str(R.string.summary_loopring_network)
    }

    @Inject
    lateinit var themeSettings: ThemeSettings
    private val themeChangeKey = ApplicationUtility.str(R.string.settings_theme_key)

    override val fragmentTitle: String
        get() = ApplicationUtility.str(R.string.settings)

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        coreLooprComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_home)
    }

    override fun getPreferenceKeysAndDefaultValues() = listOf(
            Pair(ThemeSettings.KEY_THEME, themeSettings.getCurrentThemeForSettings()),
            Pair(SCREEN_KEY_SECURITY, SUMMARY_VALUE_SECURITY_SCREEN),
            Pair(SCREEN_KEY_ETHEREUM_FEES, SUMMARY_VALUE_ETHEREUM_FEES_SCREEN),
            Pair(SCREEN_KEY_LOOPRING_FEES, SUMMARY_VALUE_LOOPRING_FEES_SCREEN),
            Pair(SCREEN_KEY_GENERAL_WALLET, SUMMARY_VALUE_GENERAL_WALLET_SCREEN),
            Pair(SCREEN_KEY_CURRENCY, SUMMARY_VALUE_CURRENCY_SCREEN),
            Pair(SCREEN_KEY_ETHEREUM_NETWORK, SUMMARY_VALUE_ETHEREUM_NETWORK_SCREEN),
            Pair(SCREEN_KEY_LOOPRING_NETWORK, SUMMARY_VALUE_LOOPRING_NETWORK_SCREEN)
    )

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        if (preference.key == themeChangeKey && themeSettings.getCurrentThemeForSettings() != value) {
            launch(UI) {
                // We need to add a delay so the dialog close animations can finish
                delay(150L)

                // We are switching themes, so we need to relaunch the activity and recreate the
                // back stack.
                activity?.let {
                    TaskStackBuilder.create(it)
                            .addNextIntent(Intent(it, CoreLooprWalletApp.mainClass))
                            .addNextIntent(Intent(it, SettingsActivity::class.java))
                            .startActivities()
                }
            }
        }

        return true
    }

    override fun getSummaryValue(preference: Preference, value: String) = when (preference.key) {
        ThemeSettings.KEY_THEME -> getSummaryForListPreference(preference as ListPreference, value)
        SCREEN_KEY_SECURITY -> SUMMARY_VALUE_SECURITY_SCREEN
        SCREEN_KEY_CURRENCY -> SUMMARY_VALUE_CURRENCY_SCREEN
        SCREEN_KEY_ETHEREUM_FEES -> SUMMARY_VALUE_ETHEREUM_FEES_SCREEN
        SCREEN_KEY_LOOPRING_FEES -> SUMMARY_VALUE_LOOPRING_FEES_SCREEN
        SCREEN_KEY_GENERAL_WALLET -> SUMMARY_VALUE_GENERAL_WALLET_SCREEN
        SCREEN_KEY_CURRENCY -> SUMMARY_VALUE_CURRENCY_SCREEN
        SCREEN_KEY_ETHEREUM_NETWORK -> SUMMARY_VALUE_ETHEREUM_NETWORK_SCREEN
        SCREEN_KEY_LOOPRING_NETWORK -> SUMMARY_VALUE_LOOPRING_NETWORK_SCREEN
        else -> throw IllegalArgumentException("Invalid key, found: ${preference.key}")
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val activity = (activity as? SettingsActivity) ?: return false
        return when (preference?.key) {
            SCREEN_KEY_SECURITY -> {
                activity.onNestedFragmentClick(SecuritySettingsFragment(), SecuritySettingsFragment.TAG)
                true
            }
            SCREEN_KEY_ETHEREUM_FEES -> {
                activity.onNestedFragmentClick(EthereumFeeSettingsFragment(), EthereumFeeSettingsFragment.TAG)
                true
            }
            SCREEN_KEY_LOOPRING_FEES -> {
                activity.onNestedFragmentClick(LoopringFeeSettingsFragment(), LoopringFeeSettingsFragment.TAG)
                true
            }
            SCREEN_KEY_GENERAL_WALLET -> {
                activity.onNestedFragmentClick(GeneralWalletSettingsFragment(), GeneralWalletSettingsFragment.TAG)
                true
            }
            SCREEN_KEY_CURRENCY -> {
                activity.onNestedFragmentClick(CurrencySettingsFragment(), CurrencySettingsFragment.TAG)
                true
            }
            SCREEN_KEY_ETHEREUM_NETWORK -> {
                activity.onNestedFragmentClick(EthereumNetworkSettingsFragment(), EthereumNetworkSettingsFragment.TAG)
                true
            }
            SCREEN_KEY_LOOPRING_NETWORK -> {
                activity.onNestedFragmentClick(LoopringNetworkSettingsFragment(), LoopringNetworkSettingsFragment.TAG)
                true
            }
            else -> false
        }
    }

}