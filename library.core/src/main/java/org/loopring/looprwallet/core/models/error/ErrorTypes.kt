package org.loopring.looprwallet.core.models.error

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object ErrorTypes {

    /**
     * The user has no network connection
     */
    const val NO_CONNECTION = 1

    /**
     * The server sent back a response (not the app's fault)
     */
    const val SERVER_ERROR = 2

    /**
     * There was an error communicating with the server. This could be due to bad data formatting,
     * inability to parse responses, missing parameters in the request/response, etc.
     *
     * Users should be prompted to submit a bug report for this type of error
     */
    const val SERVER_COMMUNICATION_ERROR = 3

    /**
     * An unknown error occurred.
     */
    const val UNKNOWN = 4

}