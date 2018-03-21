package com.caplaninnovations.looprwallet.extensions

import com.google.gson.Gson
import com.google.gson.JsonElement

/**
 * Created by Corey Caplan on 1/31/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * @return An object of type [T], by inferring the class with a *reified* type.
 */
inline fun <reified T> Gson.fromJson(json: JsonElement): T {
    return this.fromJson(json, T::class.java)
}

/**
 * @return An object of type [T], by inferring the class using a reified type
 */
inline fun <reified T> Gson.fromJson(json: String): T {
    return this.fromJson(json, T::class.java)
}