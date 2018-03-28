package com.caplaninnovations.looprwallet.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.support.v7.preference.PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.models.android.settings.LooprSettings

/**
 * Created by Corey Caplan on 3/27/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object PreferenceUtility {

    fun setDefaultValues() {
        setDefaultValues(R.xml.settings_home)
        setDefaultValues(R.xml.settings_security)
        setDefaultValues(R.xml.settings_ethereum_fees)
        // TODO LRC fees
        // TODO wallet
        setDefaultValues(R.xml.settings_currency)
        // TODO Ethereum Network
        // TODO Loopring Network
    }

    /**
     * Similar to [.setDefaultValues] but allows
     * the client to provide the filename and mode of the shared preferences
     * file.
     *
     * @param resId The resource ID of the preference XML file.
     *
     * Note: this will NOT reset preferences back to their default
     * values. For that functionality, use
     * [PreferenceManager.getDefaultSharedPreferences]
     * and clear it followed by a call to this method with this
     * parameter set to true.
     *
     * @see .setDefaultValues
     * @see .setSharedPreferencesName
     * @see .setSharedPreferencesMode
     */
    @SuppressLint("RestrictedApi")
    private fun setDefaultValues(resId: Int) {
        val context = LooprWalletApp.context
        val defaultValueSp = context.getSharedPreferences(
                KEY_HAS_SET_DEFAULT_VALUES, Context.MODE_PRIVATE)

        if (!defaultValueSp.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
            val pm = PreferenceManager(context)
            pm.preferenceDataStore = LooprSettings.getInstance(context).preferenceDataStore
            pm.sharedPreferencesName = getDefaultSharedPreferencesName(context)
            pm.sharedPreferencesMode = defaultSharedPreferencesMode
            pm.inflateFromResource(context, resId, null)

            defaultValueSp.edit()
                    .putBoolean(KEY_HAS_SET_DEFAULT_VALUES, true)
                    .apply()
        }
    }

    private fun getDefaultSharedPreferencesName(context: Context): String {
        return context.packageName + "_preferences"
    }

    private const val defaultSharedPreferencesMode = Context.MODE_PRIVATE

}