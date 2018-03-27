package com.caplaninnovations.looprwallet.fragments.security

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.longToast
import com.caplaninnovations.looprwallet.handlers.NumberPadHandler
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings
import com.caplaninnovations.looprwallet.models.android.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
import com.caplaninnovations.looprwallet.models.android.settings.UserPinSettings
import kotlinx.android.synthetic.main.fragment_security_pin.*
import javax.inject.Inject

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A fragment for the user to enter a new form of security. This could be used
 * for changing your PIN (after verifying your old one), creating a PIN (from scratch) or other
 * security features in the future.
 */
class EnterNewSecurityFragment : BaseSecurityFragment(), NumberPadHandler.NumberPadActionListener {

    /**
     * An interface used for notifying listeners that a new secruity type has been successfully
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
        private const val KEY_CURRENT_INPUT = "_CURRENT_INPUT"

        private const val KEY_SECURITY_TYPE = "_SECURITY_TYPE"

        fun createPinInstance() = EnterNewSecurityFragment().apply {
            arguments = Bundle().apply { putString(KEY_SECURITY_TYPE, TYPE_PIN_SECURITY) }
        }

    }

    override val securityType: String
        get() = arguments!!.getString(KEY_SECURITY_TYPE)

    /**
     * The PIN that was *already* entered by the user and will be compared against [currentPin].
     */
    var enteredPin: String? = null

    /**
     * The PIN that's currently being inputted by the user.
     */
    var currentPin: String = ""

    val isConfirmingEnteredPin: Boolean
        get() = enteredPin != null

    override val isDecimalVisible = false

    @Inject
    lateinit var securitySettings: SecuritySettings

    @Inject
    lateinit var userPinSettings: UserPinSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LooprWalletApp.dagger.inject(this)

        enteredPin = savedInstanceState?.getString(KEY_ENTERED_PIN)
        currentPin = savedInstanceState?.getString(KEY_CURRENT_INPUT) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindPinTitleText()

        NumberPadHandler.setupNumberPad(this, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_ENTERED_PIN, enteredPin)
        outState.putString(KEY_CURRENT_INPUT, currentPin)
    }

    override fun onNumberClick(number: String) {
        currentPin += number
        bindCurrentPinToDrawable()
        if (currentPin.length == 4) submitPin()
    }

    override fun onDecimalClick() = throw NotImplementedError()

    override fun onBackspaceClick() {
        when {
            currentPin.isNotEmpty() -> {
                currentPin = currentPin.substring(0, currentPin.length - 1)
                bindCurrentPinToDrawable()
            }
        }
    }

// MARK - Private Methods

    private fun submitPin() {

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
            else -> resetPins()
        }

        bindPinTitleText()
        bindCurrentPinToDrawable()
    }

    private fun bindPinTitleText() = when (securityType) {
        TYPE_PIN_SECURITY -> when {
            isConfirmingEnteredPin -> fragmentSecurityPinTitleLabel.setText(R.string.confirm_your_pin)
            else -> fragmentSecurityPinTitleLabel.setText(R.string.enter_a_new_pin)
        }
        else -> throw IllegalArgumentException("Invalid security type, found: $securityType")
    }

    private fun bindCurrentPinToDrawable() {
        setPinCircle(fragmentSecurityPinCircle1, 1)
        setPinCircle(fragmentSecurityPinCircle2, 2)
        setPinCircle(fragmentSecurityPinCircle3, 3)
        setPinCircle(fragmentSecurityPinCircle4, 4)
    }

    /**
     * Sets the PIN's circle (to filled or unfilled), based on the [minSize].
     */
    private fun setPinCircle(image: ImageView, minSize: Int) = when {
        currentPin.length >= minSize -> image.setImageResource(R.drawable.primary_color_circle_filled)
        else -> image.setImageResource(R.drawable.primary_color_circle)
    }

}