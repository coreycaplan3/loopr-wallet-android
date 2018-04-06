package org.loopring.looprwallet.core.utilities

import org.loopring.looprwallet.core.BuildConfig

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object BuildUtility {

    const val BUILD_DEBUG = "debug"
    const val BUILD_STAGING = "staging"
    const val BUILD_RELEASE = "release"

    const val FLAVOR_MOCKNET = "mocknet"
    const val FLAVOR_TESTNET = "testnet"
    const val FLAVOR_MAINNET = "mainnet"

    var BUILD_TYPE = BuildConfig.BUILD_TYPE
    var BUILD_FLAVOR = BuildConfig.ENVIRONMENT
    var SECURITY_LOCKOUT_TIME = BuildConfig.SECURITY_LOCKOUT_TIME

}