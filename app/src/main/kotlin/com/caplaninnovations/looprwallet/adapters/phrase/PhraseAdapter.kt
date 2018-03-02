package com.caplaninnovations.looprwallet.adapters.phrase

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.adapters.ItemTouchAdapter
import com.caplaninnovations.looprwallet.adapters.OnStartDragListener
import com.caplaninnovations.looprwallet.utilities.inflate
import java.util.*


/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class PhraseAdapter(
        private val phrase: List<String>,
        private val onWordRemoved: (String) -> Unit,
        private val onStartDragListener: OnStartDragListener
) : RecyclerView.Adapter<PhraseViewHolder>(), ItemTouchAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        return PhraseViewHolder(parent.inflate(R.layout.view_holder_phrase))
    }

    override fun getItemCount(): Int = phrase.size

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        holder.bind(phrase[position], onStartDragListener)
    }

    override fun onItemDismiss(position: Int) = onWordRemoved.invoke(phrase[position])

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(phrase, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

}