package com.caplaninnovations.looprwallet.adapters.phrase

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.adapters.ItemTouchAdapter
import com.caplaninnovations.looprwallet.adapters.OnStartDragListener
import com.caplaninnovations.looprwallet.extensions.inflate
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param phrase A list of words that will be used to populate this adapter
 * @param onWordRemoved A function that is called when a word is removed from the adapter. This
 * method's responsibility is only to propagate changes to things *aside* from [PhraseAdapter],
 * since the adapter manages the word being removed internally.
 * @param onStartDragListener A listener used for tracking drag and swipes to the items in this
 * adapter
 */
class PhraseAdapter(
        private val phrase: ArrayList<String>,
        private val onWordRemoved: () -> Unit,
        private val onStartDragListener: OnStartDragListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapter {

    companion object {
        private const val KEY_EMPTY = 0
        private const val KEY_PHRASE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            KEY_PHRASE -> PhraseViewHolder(parent.inflate(R.layout.view_holder_phrase), onStartDragListener)
            KEY_EMPTY -> PhraseEmptyViewHolder(parent.inflate(R.layout.view_holder_phrase_empty))
            else -> throw IllegalArgumentException("Invalid viewType, found $viewType")
        }
    }

    override fun getItemCount(): Int = if (phrase.isEmpty()) 1 else phrase.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PhraseViewHolder)?.bind(phrase[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && phrase.isEmpty()) {
            KEY_EMPTY
        } else {
            KEY_PHRASE
        }
    }

    /**
     * Adds the word to the [phrase] list and changes the adapter, if necessary
     *
     * @param word The word that was added to the list
     */
    fun onWordAdded(word: String) {
        phrase.add(word)

        if (phrase.size == 1) {
            // We need to get rid of the empty-state ViewHolder and add the new one
            notifyItemRemoved(0)
            notifyItemInserted(0)
        } else {
            notifyItemInserted(phrase.size - 1)
        }
    }

    override fun onItemDismiss(position: Int) {
        phrase.remove(phrase[position])
        notifyItemRemoved(position)

        if (position < phrase.size) {
            // There are items after the removed item's position
            notifyItemRangeChanged(position, phrase.size - position)
        }

        if (phrase.isEmpty()) {
            notifyItemInserted(0)
        }

        onWordRemoved.invoke()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(phrase, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        // To allow the items to be bound again
        notifyItemChanged(fromPosition)
        notifyItemChanged(toPosition)

        return true
    }

}