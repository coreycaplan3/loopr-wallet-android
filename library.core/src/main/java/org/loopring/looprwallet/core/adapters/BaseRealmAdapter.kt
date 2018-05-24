package org.loopring.looprwallet.core.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.*
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.extensions.like
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import kotlin.reflect.KProperty

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
        const val TYPE_LOADING_INITIAL = 1
        const val TYPE_EMPTY = 2
        const val TYPE_DATA = 3
        const val TYPE_HEADER = 4
        const val TYPE_LOADING_END = 5
        const val TYPE_PAGING_END = 6
    }

    var containsHeader: Boolean = false
        set(value) {
            if (field != value) {
                notifyDataSetChanged()
                field = value
            }
        }

    abstract var pager: LooprAdapterPager<T>
        protected set

    private var mData: OrderedRealmCollection<T>? = null

    val data: OrderedRealmCollection<T>?
        get() {
            val data = mData
            return when {
                data != null && data.isValid -> data
                else -> null
            }
        }

    override var layoutInflater: LayoutInflater? = null

    /**
     * A function that is invoked when the user scrolls to the bottom of the container and wants to
     * load more data
     */
    var onLoadMore: () -> Unit = {}

    private var isFiltering: Boolean = false

    /**
     * Filters the data using a like query, based on the given [property] and [value].
     */
    fun filterData(propertyList: List<KProperty<*>> = listOf(), property: KProperty<String>, value: String) {
        isFiltering = true
        val data = pager.data
        if (data == null || !data.isValid) {
            return
        }

        mData = data.where()
                .like(propertyList, property, value)
                .findAllAsync()

        updateData(mData)
    }

    /**
     * Clears the filter so the original data is re-bound to the adapter (before the filter was
     * applied)
     */
    fun clearFilter() {
        isFiltering = false
        updateData(pager.data)
    }

    final override fun getItemViewType(position: Int): Int {
        val data = data
        val containsMoreData = pager.containsMoreData

        return when {
            data == null -> TYPE_LOADING_INITIAL
            data.size == 0 -> TYPE_EMPTY
            position == 0 && containsHeader -> TYPE_HEADER
            position == itemCount - 1 && containsMoreData == true ->
                // We are at the last position (after the data), but there's still more to load
                TYPE_LOADING_END
            position == itemCount - 1 && containsMoreData != null && !containsMoreData ->
                // We are at the last position (after the data), and there's no data to load
                TYPE_PAGING_END
            else -> TYPE_DATA
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        return when (viewType) {
            TYPE_LOADING_INITIAL -> LoadingInitialViewHolder(inflater.inflate(R.layout.view_holder_loading_initial, parent, false))
            TYPE_EMPTY -> onCreateEmptyViewHolder(parent)
            TYPE_DATA -> onCreateDataViewHolder(parent)
            TYPE_HEADER -> onCreateHeaderViewHolder(parent)
            TYPE_LOADING_END -> LoadingEndViewHolder(inflater.inflate(R.layout.view_holder_loading_end, parent, false))
            TYPE_PAGING_END -> PagingEndViewHolder(inflater.inflate(R.layout.view_holder_paging_end, parent, false))
            else -> throw IllegalArgumentException("Invalid viewType, found $viewType")
        }
    }

    abstract fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    open fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw NotImplementedError("This exception is only thrown if this function was forgotten and should be implemented!")
    }

    final override fun getItemCount(): Int = data?.let {
        val containsMoreData = pager.containsMoreData
        val addition = when {
            it.size == 0 -> 1
            containsMoreData != null && containsHeader -> 2
            containsMoreData != null || containsHeader -> 1
            else -> 0
        }
        return@let it.size + addition
    } ?: 1

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingEndViewHolder) {
            onLoadMore()
            return
        }

        val newPosition = position + dataOffsetPosition
        val data = data
        if (data != null && newPosition < data.size && newPosition >= 0) {
            onBindViewHolder(holder, position, data[newPosition])
        } else {
            onBindViewHolder(holder, position, null)
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

    /**
     * Called during [onBindViewHolder].
     *
     * @param holder The ViewHolder to be bound
     * @param index The index at which this ViewHolder currently resides
     * @param item The item at the given position or null if the item could not be retrieved,
     * was out bounds, etc.
     */
    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: T?)

    // MARK - Private Methods

    /**
     * Returns the item associated with the specified position.
     * Can return `null` if provided Realm instance by [OrderedRealmCollection] is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, `null` if adapter data is not valid.
     */
    fun getItem(index: Int): T? = data?.get(index)

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new [OrderedRealmCollection] to display.
     */
    fun updateData(data: OrderedRealmCollection<T>?) = synchronized(this) {
        removeChangeListener()
        this.mData = data
        notifyDataSetChanged()
        addChangeListener()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        addChangeListener()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        removeChangeListener()
    }

    private val changeListener by lazy {
        OrderedRealmCollectionChangeListener { _: RealmResults<T>, changeSet: OrderedCollectionChangeSet? ->

            // Null Changes means an async query returns for the first time
            if (changeSet == null) {
                notifyDataSetChanged()
                return@OrderedRealmCollectionChangeListener
            }

            /**
             * We need to push the index forward, so we negate [dataOffsetPosition]
             */
            val offset = -dataOffsetPosition

            // For deletions, the adapter has to be notified in reverse order.
            val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {
                val range = deletions[i]
                notifyItemRangeRemoved(range.startIndex + offset, range.length)
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
                notifyItemRangeInserted(range.startIndex + offset, range.length)
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
                notifyItemRangeChanged(range.startIndex + offset, range.length)
            }

        }
    }

    private fun addChangeListener() {
        val data = mData
        when {
            data is RealmResults -> data.addChangeListener { _: RealmResults<T> ->
                if (!isFiltering) {
                    this.mData = data
                }
                notifyDataSetChanged()
            }
            data != null -> throw IllegalArgumentException("Invalid type, found: ${data::class.simpleName}")
            else -> Unit
        }
    }

    private fun removeChangeListener() {
        val data = mData
        when {
            data is RealmResults -> data.removeChangeListener(changeListener)
            data != null -> throw IllegalArgumentException("Invalid type, found: ${data::class.simpleName}")
            else -> Unit
        }
    }

    private class LoadingInitialViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    private class LoadingEndViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    private class PagingEndViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}