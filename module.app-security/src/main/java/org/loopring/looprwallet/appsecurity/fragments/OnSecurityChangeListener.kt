package org.loopring.looprwallet.appsecurity.fragments

/**
 * Created by Corey Caplan on 3/28/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A listener used for transferring security change events back to the
 * implementor. This includes things like:
 * - The user enabling or disabling a PIN
 * - The user enabling or disabling other forms of security
 */
interface OnSecurityChangeListener {

    /**
     * Called when the user has enabled security for the app.
     *
     * @param securityType The type of security that was just enabled
     */
    fun onSecurityEnabled(securityType: String)

    /**
     * Called when the user has disabled security for the app.
     */
    fun onSecurityDisabled()
}