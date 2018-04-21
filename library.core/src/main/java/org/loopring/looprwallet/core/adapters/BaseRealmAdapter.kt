package org.loopring.looprwallet.core.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.realm.*
import io.realm.kotlin.addChangeListener
import io.realm.kotlin.removeChangeListener
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.inflate

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
        const val TYPE_LOADING = 0
        const val TYPE_EMPTY = 1
        const val TYPE_DATA = 2
    }

    var extraDataToPositionList: List<Pair<RealmModel, Int>>? = null
        set(value) {
            field = value

            // Clear the old listeners
            listeners.forEach { it.first.removeChangeListener(it.second) }
            listeners.clear()

            value?.forEach { dataToPositionPair ->
                val listener = RealmObjectChangeListener<RealmModel> { _, changeSet ->
                    when {
                        changeSet?.isDeleted == true -> notifyItemRemoved(dataToPositionPair.second)
                        else -> notifyItemChanged(dataToPositionPair.second)
                    }
                }

                listeners.add(dataToPositionPair.first to listener)
                Unit
            }
        }

    /**
     * Extra listeners for listening to object changes besides from main type declared as [T].
     */
    private val listeners = arrayListOf<Pair<RealmModel, RealmObjectChangeListener<RealmModel>>>()

    /**
     * The total number of items that can be loaded and stored in *data*. This represents the total
     * amount of data, not the current page size or the amount loaded in a network call. This  field
     * is then used to create/check the viewType, given a position. Mainly to distinguish between
     * whether or not we should "load more" data after scrolling beyond the current data's
     * capacity.
     *
     * Set this field to *null* if it shouldn't be used. For example:
     * - The app is loading data only locally and we know there's nothing more to load.
     * - We don't know how many items there are in the list
     */
    abstract val totalItems: Int?

    override fun getItemViewType(position: Int): Int {
        val data = data ?: return TYPE_LOADING

        return when {
            !data.isValid -> TYPE_LOADING
            data.size == 0 -> TYPE_EMPTY
            position == itemCount - 1 && containsMoreData() ->
                // We are at the last item, and there's still more data to load
                TYPE_LOADING
            else -> TYPE_DATA
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(parent.inflate(R.layout.view_holder_loading))
            TYPE_EMPTY -> onCreateEmptyViewHolder(parent)
            else -> onCreateDataViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = data
        val offsetPosition = getDataOffset(position)
        if (data != null && offsetPosition != null) {
            onBindViewHolder(holder, position, data[offsetPosition])
        }
    }

    /**
     * Calculates an offset from the given position, if necessary for binding data and indexing into
     * the list of data.
     *
     * For example, we need to bind index 1, but the 0th index is a header item and unrelated to
     * the data in this adapter. In that situation, the data offset is *-1*, since we need to move
     * the position back by 1 to properly index into the [adapterData] list.
     *
     * @param position The position at which data needs to be bound, according to [onBindViewHolder].
     *
     * @return The offset position or null if **NO** data should be retrieved at this position
     */
    abstract fun getDataOffset(position: Int): Int?

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: T?)

    override fun getItemCount(): Int {
        // We return an extra item to account for the loading view holder
        return data?.let { getItemCountForOnlyData(it) } ?: 1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        listeners.forEach { it.first.addChangeListener(it.second) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        listeners.forEach { it.first.removeChangeListener(it.second) }
    }

    // MARK - Protected Methods

    /**
     * Gets the number of items in the adapter based on the amount of data available
     */
    protected fun getItemCountForOnlyData(data: RealmCollection<T>): Int {
        return data.size.let {
            when {
                containsMoreData() ->
                    // There's still some items left to load
                    it + 1
                else -> it
            }
        }
    }

    // MARK - Private Methods

    /**
     * @return True if this adapter contains more data to load from the network or false otherwise
     */
    private fun containsMoreData(): Boolean {
        val totalItems = totalItems ?: return false
        return data?.size?.let { it < totalItems } ?: false
    }

    private class LoadingViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}