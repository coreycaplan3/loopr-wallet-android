package com.caplaninnovations.looprwallet.extensions

/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create extension methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

fun <T> Pair<T?, T?>.isBothNonNull() = this.first != null && this.second != null

/**
 * If [T] is null, this object is mapped to a non-optional type [T]. If it's not null, [T] is
 * just returned.
 *
 * @param mapper The function that will be used to map this null object to it's non-optional
 * counterpart. Note, this function is **NOT** called if [T] is **NOT** null.
 */
inline fun <T : Any> T?.mapIfNull(mapper: () -> T): T {
    return this ?: mapper()
}

/**
 * Executes the [block] if both items in the pair are non-null
 *
 * @param block The block to execute if both parameters are  non-null.
 */
inline fun <T, U> Pair<T?, U?>.allNonNull(block: (pair: Pair<T, U>) -> Unit) {
    val first = this.first ?: return
    val second = this.second ?: return

    block(Pair(first, second))
}