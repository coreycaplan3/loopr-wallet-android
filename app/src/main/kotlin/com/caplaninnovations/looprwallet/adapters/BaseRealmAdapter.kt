package com.caplaninnovations.looprwallet.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.inflate
import io.realm.RealmModel
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
abstract class BaseRealmAdapter<T : RealmModel> :
        RealmRecyclerViewAdapter<T, RecyclerView.ViewHolder>(null, true, true) {

    companion object {
        private const val TYPE_LOADING = 0
        private const val TYPE_EMPTY = 1
        private const val TYPE_DATA = 2
    }

    final override fun getItemViewType(position: Int): Int {
        val data = data ?: return TYPE_LOADING

        return when {
            data.isEmpty() -> TYPE_EMPTY
            else -> TYPE_DATA
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(parent.inflate(R.layout.view_holder_loading))
            TYPE_EMPTY -> onCreateEmptyViewHolder(parent)
            TYPE_DATA -> onCreateDataViewHolder(parent)
            else -> throw IllegalArgumentException("Invalid viewType, found: $viewType")
        }
    }

    abstract fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = data
        if (data != null && position < data.size) {
            onBindViewHolder(holder, data[position])
        }
    }

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: T)

    final override fun getItemCount(): Int {
        return data.let { it?.size ?: 1 }
    }

    class LoadingViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}