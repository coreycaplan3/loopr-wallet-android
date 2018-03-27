package com.caplaninnovations.looprwallet.fragments.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility

/**
 * Created by Corey on 3/26/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class EthereumNetworkSettingsFragment: BaseSettingsFragment() {

    companion object {

        val TAG: String = EthereumNetworkSettingsFragment::class.java.simpleName

    }

    override val fragmentTitle = ApplicationUtility.str(R.string.ethereum_network)

    override fun getPreferenceKeysAndDefaultValuesForListeners(): List<Pair<String, String>> {
        TODO("not implemented") // TODO
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        TODO("not implemented") // TODO
    }

    override fun onPreferenceValueChange(preference: Preference, value: String): Boolean {
        TODO("not implemented") // TODO
    }

    override fun getSummaryValue(preference: Preference, value: String): String {
        TODO("not implemented") // TODO
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("not implemented") // TODO
    }

}