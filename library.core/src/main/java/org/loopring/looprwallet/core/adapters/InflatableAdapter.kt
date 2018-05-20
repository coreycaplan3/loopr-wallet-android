package org.loopring.looprwallet.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by corey on 5/8/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: For storing and standardizing the safe **reuse** of layout inflaters in
 * RecyclerView.Adapter
 *
 */
interface InflatableAdapter {

    var layoutInflater: LayoutInflater?

    /**
     * Gets the layoutInflater and initializes it, if necessary
     */
    fun getInflater(parent: ViewGroup): LayoutInflater = synchronized(this) {
        return this.layoutInflater ?: LayoutInflater.from(parent.context).also {
            this.layoutInflater = it
        }
    }

}