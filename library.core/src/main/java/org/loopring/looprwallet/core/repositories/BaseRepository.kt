package org.loopring.looprwallet.core.repositories

import kotlinx.coroutines.experimental.android.HandlerContext
import org.loopring.looprwallet.core.models.android.architecture.IO

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
    fun add(data: T, context: HandlerContext = IO)

    /**
     * Adds new data to the database or updates existing data. This method may be called from any
     * thread.
     */
    fun addList(data: List<T>, context: HandlerContext = IO)

    /**
     * Removes the data from the database. This method may be called from a any thread.
     */
    fun remove(data: T, context: HandlerContext = IO)

    /**
     * Removes the list of data from the database. This method may be called from a any thread.
     */
    fun remove(data: List<T>, context: HandlerContext = IO)

}