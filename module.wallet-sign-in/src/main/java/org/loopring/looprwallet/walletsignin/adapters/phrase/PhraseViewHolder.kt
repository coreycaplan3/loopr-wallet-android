package org.loopring.looprwallet.walletsignin.adapters.phrase

import android.support.annotation.DimenRes
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import org.loopring.looprwallet.walletsignin.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_phrase.*
import org.loopring.looprwallet.core.adapters.ItemTouchViewHolder
import org.loopring.looprwallet.core.adapters.OnStartDragListener


/**
 * Created by Corey on 3/2/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show a phrase in a 12-word phrase list and allow the user to drag, swipe,
 * and select items.
 */
internal class PhraseViewHolder(itemView: View, dragStartListener: OnStartDragListener)
    : RecyclerView.ViewHolder(itemView), LayoutContainer, ItemTouchViewHolder {

    override val containerView: View?
        get() = itemView


    init {
        phraseReorderButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dragStartListener.onStartDrag(this)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    fun bind(word: String) {
        phraseWordLabel.text = String.format("%d - %s", adapterPosition + 1, word)
    }

    override fun onItemSelected() {
        setCardElevation(R.dimen.card_elevation_selected)
        phraseCard.alpha = 0.85F
    }

    override fun onItemClear() {
        setCardElevation(R.dimen.card_elevation)
        phraseCard.alpha = 1F
    }

    // Mark - Private Methods

    private fun setCardElevation(@DimenRes elevation: Int) {
        phraseCard.cardElevation = itemView.context.resources.getDimension(elevation)
    }

}