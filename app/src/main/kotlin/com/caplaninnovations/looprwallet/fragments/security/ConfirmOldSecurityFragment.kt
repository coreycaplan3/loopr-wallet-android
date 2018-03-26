package com.caplaninnovations.looprwallet.fragments.security

import android.os.Bundle
import com.caplaninnovations.looprwallet.application.LooprWalletApp

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
                    arguments = Bundle().apply { putInt(KEY_CONFIRM_SECURITY_TYPE, type) }
                }
    }

    override val securityType: String
        get() {
            return LooprWalletApp.dagger.securitySettings.getCurrentSecurityType()
        }

}