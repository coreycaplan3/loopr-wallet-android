package org.loopring.looprwallet.core.fragments.security

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_security_pin.*
import kotlinx.android.synthetic.main.number_pad.*
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.presenters.NumberPadPresenter
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.TYPE_DEFAULT_VALUE_SECURITY
import org.loopring.looprwallet.core.models.settings.SecuritySettings.Companion.TYPE_PIN_SECURITY
import org.loopring.looprwallet.core.models.settings.UserPinSettings
import javax.inject.Inject

/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
abstract class BaseSecurityFragment : BaseFragment(), NumberPadPresenter.NumberPadActionListener {

    companion object {

        private const val KEY_CURRENT_INPUT = "_CURRENT_INPUT"
    }

    /**
     * The type of security page to show.
     *
     * @see TYPE_PIN_SECURITY
     */
    abstract val securityType: String

    final override val isDecimalVisible = false

    final override val layoutResource: Int
        get() = when (securityType) {
            TYPE_PIN_SECURITY -> R.layout.fragment_security_pin
            else -> throw IllegalArgumentException("Invalid security type, found: $securityType")
        }

    /**
     * The PIN that's currently being inputted by the user.
     */
    var currentPin: String = ""

    @Inject
    lateinit var securitySettings: SecuritySettings

    @Inject
    lateinit var userPinSettings: UserPinSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreLooprComponent.inject(this)

        currentPin = savedInstanceState?.getString(KEY_CURRENT_INPUT) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Perform view-specific setup
        when (securityType) {
            TYPE_PIN_SECURITY -> {
                numberPadDecimal.visibility = View.GONE
                NumberPadPresenter.setupNumberPad(this, this)
            }
        }

    }

    final override fun onNumberClick(number: String) {
        currentPin += number
        bindCurrentPinToDrawable()
        if (currentPin.length == 4) onSubmitPin()
    }

    override fun onDecimalClick() = throw NotImplementedError()

    override fun onBackspaceClick() {
        if (currentPin.isNotEmpty()) {
            currentPin = currentPin.substring(0, currentPin.length - 1)
            bindCurrentPinToDrawable()
        }
    }

    /**
     * Called when the PIN's length is now of size 4
     */
    abstract fun onSubmitPin()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_CURRENT_INPUT, currentPin)
    }

    // MARK - Private Methods

    /**
     * Sets the PIN's circle (to filled or unfilled), based on the length of [currentPin].
     */
    protected fun bindCurrentPinToDrawable() {

        fun setPinCircle(image: ImageView, minSize: Int) = when {
            currentPin.length >= minSize -> image.setImageResource(R.drawable.primary_color_circle_filled)
            else -> image.setImageResource(R.drawable.primary_color_circle)
        }

        setPinCircle(fragmentSecurityPinCircle1, 1)
        setPinCircle(fragmentSecurityPinCircle2, 2)
        setPinCircle(fragmentSecurityPinCircle3, 3)
        setPinCircle(fragmentSecurityPinCircle4, 4)
    }

}