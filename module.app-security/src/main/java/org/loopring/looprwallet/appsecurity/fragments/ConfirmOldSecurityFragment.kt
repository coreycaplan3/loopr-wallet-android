package org.loopring.looprwallet.appsecurity.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.os.bundleOf
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.handlers.NumberPadHandler
import org.loopring.looprwallet.core.models.settings.SecuritySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str


/**
 * Created by Corey Caplan on 3/25/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class used when the user must enter their old security settings. This could
 * occur during the following:
 * - Before disabling security settings
 * - Before changing their security settings (IE changing their PIN)
 * - When entering the app (as a "lock" page)
 * - When viewing sensitive information, like a private key
 */
class ConfirmOldSecurityFragment : BaseSecurityFragment() {

    /**
     * A listener used to pass security confirmation events back to this implementor.
     */
    interface OnSecurityConfirmedListener {

        /**
         * Called when the user has successfully confirmed their credentials (IE their PIN). This
         * should only occur while the user is trying to do the following operations:
         * - Disabling security
         * - Changing security settings
         * - Unlocking the app
         * - Viewing their private key
         */
        fun onSecurityConfirmed()
    }

    companion object {

        val TAG: String = ConfirmOldSecurityFragment::class.java.simpleName

        /**
         * A key used to track the type of interaction being made with this
         * [ConfirmOldSecurityFragment].
         *
         * @see TYPE_DISABLE_SECURITY
         * @see TYPE_CHANGE_SECURITY_SETTINGS
         * @see TYPE_UNLOCK_APP
         * @see TYPE_VIEWING_PRIVATE_KEY
         */
        private const val KEY_CONFIRM_SECURITY_TYPE = "_CONFIRM_SECURITY_TYPE"

        /**
         * The user is trying to disable their security settings
         */
        private const val TYPE_DISABLE_SECURITY = 1

        /**
         * The user is trying to change their security settings
         */
        private const val TYPE_CHANGE_SECURITY_SETTINGS = 2

        /**
         * The user is trying to open the app after it has been locked
         */
        private const val TYPE_UNLOCK_APP = 3

        /**
         * The user is requesting to view their private key
         */
        private const val TYPE_VIEWING_PRIVATE_KEY = 4

        fun createDisableSecurityInstance() = createInstance(TYPE_DISABLE_SECURITY)

        fun createChangeSecuritySettings() = createInstance(TYPE_CHANGE_SECURITY_SETTINGS)

        fun createUnlockAppInstance() = createInstance(TYPE_UNLOCK_APP)

        fun createViewPrivateKeyInstance() = createInstance(TYPE_VIEWING_PRIVATE_KEY)

        private fun createInstance(type: Int) =
                ConfirmOldSecurityFragment().apply {
                    arguments = bundleOf(KEY_CONFIRM_SECURITY_TYPE to type)
                }
    }

    override val securityType: String
        get() {
            return securitySettings.getCurrentSecurityType()
        }

    private val confirmationType: Int
        get() = arguments?.getInt(KEY_CONFIRM_SECURITY_TYPE)!!

    var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isUpNavigationEnabled = confirmationType != TYPE_UNLOCK_APP
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userPinSettings.isUserLockedOut()) {
            onUserLockout()
        } else {
            bindSecurityLabel()
        }
    }

    override fun onSubmitPin() {
        when {
            userPinSettings.checkPinAndIncrementAttemptsIfFailure(currentPin) -> {
                (activity as? OnSecurityConfirmedListener)?.onSecurityConfirmed()
            }
            userPinSettings.isUserLockedOut() ->
                // By calling checkPinAndIncrementAttemptsIfFailure and reaching here, the user may
                // be locked out.
                onUserLockout()
        }
    }

    /**
     * This method should be called when the back button is pressed. Reason being, we may need to
     * exit the app if the user is attempting to go back while unlocking the app from a cold boot.
     */
    fun onBackButtonPressed() {
        if (confirmationType == TYPE_UNLOCK_APP) {
            val intent = Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        countDownTimer?.cancel()
        countDownTimer = null
    }

    // MARK - Private Methods

    private fun bindSecurityLabel() = when (securityType) {
        SecuritySettings.TYPE_PIN_SECURITY -> {
            val text = when (confirmationType) {
                TYPE_DISABLE_SECURITY -> ApplicationUtility.str(R.string.enter_old_pin_disable_security)
                TYPE_CHANGE_SECURITY_SETTINGS -> ApplicationUtility.str(R.string.enter_old_pin_create_new_one)
                TYPE_UNLOCK_APP -> ApplicationUtility.str(R.string.enter_pin_to_enter_app)
                TYPE_VIEWING_PRIVATE_KEY -> ApplicationUtility.str(R.string.enter_pin_view_private_key)
                else -> throw IllegalArgumentException("Invalid confirmationType, found: $confirmationType")
            }

            fragmentSecurityPinTitleLabel.text = text
        }

        else -> throw IllegalArgumentException("Invalid securityType, found: $securityType")
    }

    private fun onUserLockout() {
        NumberPadHandler.disableNumberPad(this)

        countDownTimer = object : CountDownTimer(userPinSettings.getLockoutTimeLeft(), 250L) {
            override fun onFinish() {
                bindSecurityLabel()
                NumberPadHandler.enableNumberPad(this@ConfirmOldSecurityFragment)
            }

            override fun onTick(millisUntilFinished: Long) {
                val minutesLeft = millisUntilFinished / (60L * 1000L)

                val text = when (minutesLeft) {
                    0L -> {
                        val secondsLeft = millisUntilFinished / 1000L
                        val formatterLockoutTimeSeconds = str(R.string.formatter_locked_out_seconds)
                        String.format(formatterLockoutTimeSeconds, secondsLeft.toString())
                    }
                    else -> {
                        val formatterLockoutTimeMinutes = str(R.string.formatter_locked_out_minutes)
                        String.format(formatterLockoutTimeMinutes, minutesLeft.toString())
                    }
                }

                fragmentSecurityPinTitleLabel.text = text
            }
        }.start()
    }

}