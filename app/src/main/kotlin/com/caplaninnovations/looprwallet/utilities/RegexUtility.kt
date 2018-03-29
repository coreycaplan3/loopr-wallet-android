package com.caplaninnovations.looprwallet.utilities

/**
 * Created by Corey Caplan on 3/12/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object RegexUtility {

    val LETTERS_REGEX = Regex("[a-zA-Z]+")

    val WHITESPACE_REGEX = Regex("\\s+")

    val NAME_REGEX = Regex("[a-zA-Z0-9][a-zA-Z0-9\\-_\\s]+")

    val PUBLIC_KEY_REGEX = Regex("0x[a-f0-9]+")

    val PRIVATE_KEY_REGEX = Regex("[a-f0-9]+")
}