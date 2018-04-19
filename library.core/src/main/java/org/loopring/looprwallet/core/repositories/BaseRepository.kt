package org.loopring.looprwallet.core.repositories

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface BaseRepository<T> {

    /**
     * Adds new data to the database or updates existing data. This method may be called from any
     * thread.
     */
    fun add(data: T)

    /**
     * Adds new data to the database or updates existing data. This method may be called from any
     * thread.
     */
    fun addList(data: List<T>)

    /**
     * Removes the data from the database. This method may be called from a any thread.
     */
    fun remove(data: T)

    /**
     * Removes the list of data from the database. This method may be called from a any thread.
     */
    fun remove(data: List<T>)

    /**
     * Called when this repository is no longer needed and can be destroyed.
     */
    fun clear()

}