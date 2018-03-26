package com.caplaninnovations.looprwallet.fragments.security

import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
abstract class BaseSecurityFragment : BaseFragment() {

    companion object {

        const val KEY_SECURITY_TYPE = "_SECURITY_TYPE"

        val SECURITY_TYPE_PIN = ApplicationUtility.str(R.string.settings_security_type_entries_values_pin)
    }

    final override val layoutResource: Int
        get() {
            val type = arguments?.getString(KEY_SECURITY_TYPE)
            return when (type) {
                SECURITY_TYPE_PIN -> R.layout.fragment_security_pin
                else -> throw IllegalArgumentException("Invalid security type, found: $type")
            }
        }

}