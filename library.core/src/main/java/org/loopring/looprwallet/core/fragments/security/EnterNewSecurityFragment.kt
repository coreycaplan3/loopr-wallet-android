package org.loopring.looprwallet.core.fragments.security

import android.os.Bundle
import android.view.View
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_security_pin.*
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.presenters.NumberPadPresenter
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment for the user to enter a new form of security. This could be used
 * for changing your PIN (after verifying your old one), creating a PIN (from scratch) or other
 * security features in the future.
 */
class EnterNewSecurityFragment : BaseSecurityFragment() {

    /**
     * An interface used for notifying listeners that a new security type has been successfully
     * inputted.
     */
    interface OnNewSecurityInputtedListener {

        /**
         * Called when a new security type was inputted into the [EnterNewSecurityFragment].
         *
         * @param securityType The new security type inputted
         */
        fun onNewSecurityInputted(securityType: String)
    }

    companion object {

        val TAG: String = EnterNewSecurityFragment::class.java.simpleName

        private const val KEY_ENTERED_PIN = "_ENTERED_PIN"

        private const val KEY_SECURITY_TYPE = "_SECURITY_TYPE"

        fun createPinInstance() = EnterNewSecurityFragment().apply {
            arguments = bundleOf(KEY_SECURITY_TYPE to TYPE_PIN_SECURITY)
        }

    }

    override val securityType: String
        get() = arguments!!.getString(KEY_SECURITY_TYPE)

    /**
     * The PIN that was *already* entered by the user and will be compared against [currentPin].
     */
    var enteredPin: String? = null

    val isConfirmingEnteredPin: Boolean
        get() = enteredPin != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enteredPin = savedInstanceState?.getString(KEY_ENTERED_PIN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (securityType) {
            SecuritySettings.TYPE_PIN_SECURITY -> {
                bindPinTitleText()
                NumberPadPresenter.setupNumberPad(this, this)
            }
            else -> throw IllegalArgumentException("Invalid securityType, found: $securityType")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_ENTERED_PIN, enteredPin)
    }

    override fun onSubmitPin() {

        fun resetPins() {
            enteredPin = null
            currentPin = ""
        }

        when {
            isConfirmingEnteredPin -> when (currentPin) {
                enteredPin -> {
                    context?.longToast(R.string.your_new_pin_is_set)
                    securitySettings.setCurrentSecurityType(securityType)
                    userPinSettings.setUserPin(currentPin)
                    (activity as? OnNewSecurityInputtedListener)?.onNewSecurityInputted(securityType)
                }
                else -> {
                    context?.longToast(R.string.error_confirm_pin_does_not_match)
                    resetPins()
                }
            }
            else -> {
                enteredPin = currentPin
            }
        }

        bindPinTitleText()
        bindCurrentPinToDrawable()
    }

    // MARK - Private Methods

    private fun bindPinTitleText() = when (securityType) {
        TYPE_PIN_SECURITY -> when {
            isConfirmingEnteredPin -> fragmentSecurityPinTitleLabel.setText(R.string.confirm_your_pin)
            else -> fragmentSecurityPinTitleLabel.setText(R.string.enter_a_new_pin)
        }
        else -> throw IllegalArgumentException("Invalid security type, found: $securityType")
    }

}