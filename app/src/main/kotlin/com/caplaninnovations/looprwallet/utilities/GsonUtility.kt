package com.caplaninnovations.looprwallet.utilities

import com.google.gson.Gson
import com.google.gson.JsonElement

/**
 * Created by Corey Caplan on 1/31/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
inline fun <reified T : Any> Gson.fromJson(json: JsonElement): T {
    return this.fromJson(json, T::class.java)
}

inline fun <reified T : Any> Gson.fromJson(json: String): T {
    return this.fromJson(json, T::class.java)
}