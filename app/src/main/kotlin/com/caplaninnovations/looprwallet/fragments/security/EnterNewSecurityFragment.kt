package com.caplaninnovations.looprwallet.fragments.security

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings
import kotlinx.android.synthetic.main.fragment_security_pin.*

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class EnterNewSecurityFragment : BaseSecurityFragment() {

    companion object {

        val TAG: String = EnterNewSecurityFragment::class.java.simpleName

        private const val KEY_ENTERED_PIN = "_ENTERED_PIN"
        private const val KEY_CURRENT_INPUT = "_CURRENT_INPUT"
        private const val KEY_IS_CONFIRMING_ENTERED_PIN = "_IS_CONFIRMING_ENTERED_PIN"

        private const val KEY_SECURITY_TYPE = "_SECURITY_TYPE"

        fun createPinInstance() = EnterNewSecurityFragment().apply {
            arguments = Bundle().apply { putString(KEY_SECURITY_TYPE, SecuritySettings.TYPE_PIN_SECURITY) }
        }

    }

    override val securityType: String
        get() = arguments!!.getString(KEY_SECURITY_TYPE)

    var enteredPin: String? = null
    var currentPin: String = ""
    var isConfirmingEnteredPin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enteredPin = savedInstanceState?.getString(KEY_ENTERED_PIN)
        currentPin = savedInstanceState?.getString(KEY_CURRENT_INPUT) ?: ""
        isConfirmingEnteredPin = savedInstanceState?.getBoolean(KEY_IS_CONFIRMING_ENTERED_PIN, false) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindPriceTitleText()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_ENTERED_PIN, enteredPin)
        outState.putString(KEY_CURRENT_INPUT, currentPin)
        outState.putBoolean(KEY_ENTERED_PIN, isConfirmingEnteredPin)
    }

    // MARK - Private Methods

    fun bindPriceTitleText() = when (securityType) {
        SecuritySettings.TYPE_PIN_SECURITY -> {
            when {
                isConfirmingEnteredPin -> securityPinTitleLabel.setText(R.string.confirm_your_pin)
                else -> securityPinTitleLabel.setText(R.string.enter_a_new_pin)
            }
        }
        else -> throw IllegalArgumentException("Invalid security type, found: $securityType")
    }

}