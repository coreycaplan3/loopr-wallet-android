package com.caplaninnovations.looprwallet.utilities

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * @return True if all of the elements in the list are **not** null or false otherwise
 */
fun <T> List<T?>.allNonnull(): Boolean = this.all { it != null }