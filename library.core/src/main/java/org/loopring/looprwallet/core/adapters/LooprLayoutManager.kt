package org.loopring.looprwallet.core.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.loopring.looprwallet.core.extensions.logi

/**
 * Created by corey on 5/28/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class LooprLayoutManager(context: Context?) : LinearLayoutManager(context) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            logi("Caught IndexOutOfBoundsException in recycler…")
        }
    }
}