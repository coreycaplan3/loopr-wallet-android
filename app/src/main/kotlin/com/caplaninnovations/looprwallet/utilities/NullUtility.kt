package com.caplaninnovations.looprwallet.utilities

/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * @return True if all of the objects in a pair are not null or false otherwise
 */
fun <T: Any> Pair<T?, T?>.isAllNonNull() = this.first != null && this.second != null

/**
 * Executes the given code block if both items in the pair are non-null
 */
inline fun <T: Any> Pair<T?, T?>.allNonNull(action: (pair: Pair<T, T>) -> Unit) {
    if (this.first != null && this.second != null)
        action(Pair(this.first!!, this.second!!))
}