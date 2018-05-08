package org.loopring.looprwallet.core.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmCollection
import io.realm.RealmModel
import org.loopring.looprwallet.core.R

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: An adapter fit for using realm's features out-of-the-box. This class has the
 * ability to add a header item that is always in the 0th position (first) in the list.
 */
abstract class BaseRealmAdapter<T : RealmModel> : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        InflatableAdapter {

    companion object {
        const val TYPE_LOADING = 0
        const val TYPE_EMPTY = 1
        const val TYPE_DATA = 2
        const val TYPE_HEADER = 3
    }

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

    var containsHeader: Boolean = false
        set(value) {
            if (field != value) {
                notifyDataSetChanged()
                field = value
            }
        }

    var data: OrderedRealmCollection<T>? = null
        private set

    override var layoutInflater: LayoutInflater? = null

    final override fun getItemViewType(position: Int): Int {
        val data = data ?: return TYPE_LOADING

        return when {
            !data.isValid -> TYPE_LOADING
            data.size == 0 -> TYPE_EMPTY
            position == 0 && containsHeader -> TYPE_HEADER
            position == itemCount && containsMoreData() ->
                // We are at the last position (after the data), but there's still more to load
                TYPE_LOADING
            else -> TYPE_DATA
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(inflater.inflate(R.layout.view_holder_loading, parent, false))
            TYPE_EMPTY -> onCreateEmptyViewHolder(parent)
            TYPE_DATA -> onCreateDataViewHolder(parent)
            TYPE_HEADER -> onCreateHeaderViewHolder(parent)
            else -> throw IllegalArgumentException("Invalid viewType, found $viewType")
        }
    }

    abstract fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    open fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw NotImplementedError("This exception is only thrown if this function was forgotten and should be implemented!")
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = data
        val newPosition = position + dataOffsetPosition
        if (data != null && newPosition >= 0 && newPosition < data.size) {
            onBindViewHolder(holder, position, data[newPosition])
        }
    }

    /**
     * Returns an offset from the given position, if necessary for binding data and indexing into
     * the list of data.
     *
     * For example, we need to bind position 1, but the 0th position is a header item and unrelated
     * to the data in this adapter. In that situation, the data offset is *-1*, since we need to
     * move the position back by 1 to properly index into the [data] list.
     *
     * @return The offset position or null if **NO** data should be retrieved at this position
     */
    val dataOffsetPosition
        get() = when (containsHeader) {
            true -> -1
            else -> 0
        }

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: T?)

    final override fun getItemCount(): Int = data?.let {
        val totalItems = totalItems
        val canLoadMore = totalItems != null && it.size < totalItems
        val addition = when {
            canLoadMore && containsHeader -> 2
            canLoadMore || containsHeader -> 1
            else -> 0
        }
        return@let it.size + addition
    } ?: 1

    // MARK - Protected Methods

    /**
     * Gets the number of items in the adapter based on the amount of data currently and if there's
     * any more available after loading more from the network.
     */
    protected fun getItemCountForOnlyData(data: RealmCollection<T>): Int = data.size.let {
        return@let when {
            containsMoreData() ->
                // There's still some items left to load
                it + 1
            else -> it
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

    /**
     * Returns the item associated with the specified position.
     * Can return `null` if provided Realm instance by [OrderedRealmCollection] is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, `null` if adapter data is not valid.
     */
    fun getItem(index: Int): T? {
        return if (isDataValid()) data?.get(index) else null
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new [OrderedRealmCollection] to display.
     */
    fun updateData(data: OrderedRealmCollection<T>?) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun isDataValid(): Boolean {
        return data != null && data!!.isValid
    }

    private class LoadingViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}