package com.caplaninnovations.looprwallet.repositories

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
     * Adds the data to the database. This method may be called from a background thread.
     */
    fun add(data: T)

    /**
     * Adds the list of data to the database. This method may be called from a background thread.
     */
    fun addList(data: List<T>)

    /**
     * Removes the data from the database. This method may be called from a background thread.
     */
    fun remove(data: T)

    /**
     * Removes the list of data from the database. This method may be called from a background
     * thread.
     */
    fun remove(data: List<T>)

    /**
     * Called when this repository is no longer need and can be garbage-collected.
     */
    fun clear()

}