package org.loopring.looprwallet.core.extensions

/**
 * Created by Corey Caplan on 2/20/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * Executes the given [block] if all elements of this list are **NOT** null.
 *
 * @param block The block that is executed if **ALL** elements are not null. This block takes
 * the list of **non-null** arguments of type [T] as a parameter.
 * @return True if all elements are not null or false otherwise.
 */
inline fun <T> List<T?>.allNonnull(block: (List<T>) -> Unit): Boolean {
    val nonNullList = this.map { it ?: return false } // Returns on bad predicate
    block(nonNullList)
    return true
}

/**
 * Executes the given code block if this list's content is the **exact** same. Meaning, the ordering
 * must also be the same.
 *
 * @param other The other list to compare *this* one against.
 * @param predicate A predicate to apply to check for the two argument's equality.
 */
inline fun <T> List<T>.allEqual(other: List<T>, predicate: (T, T) -> Boolean): Boolean {
    if (this.size != other.size) {
        return false
    }

    this.mapIndexed { index, item -> if (!predicate(item, other[index])) return false }
    return true
}

/**
 * Returns index of the first element matching the given [predicate], or null if the list does not contain such element.
 */
inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    this.forEachIndexed { index, data ->
        if (predicate(data)) return index
    }

    return null
}

/**
 * Finds an element based on the supplied predicate and updates the index with [data]. If the
 * element is not found, nothing occurs.
 *
 * @return True if an update (in place) occurred or false if it one did not and *instead* the
 * element was added to the list.
 */
inline fun <T> MutableList<T>.insertOrUpdate(data: T, predicate: (T) -> Boolean): Boolean {
    val index = indexOfFirstOrNull(predicate)
    return when {
        index != null -> {
            this[index] = data
            true
        }
        else -> {
            this.add(data)
            false
        }
    }
}

/**
 * Finds an element based on the supplied predicate and updates the index with [data]. If the
 * element is not found, nothing occurs.
 *
 * @return True if an update occurred or false if it one did not.
 */
inline fun <T> MutableList<T>.update(data: T, predicate: (T) -> Boolean): Boolean {
    val index = indexOfFirstOrNull(predicate)
    return when {
        index != null -> {
            this[index] = data
            true
        }
        else -> false
    }
}

/**
 * Converts this list to an array list
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return if (this is ArrayList) return this
    else ArrayList(this.toMutableList())
}