package com.caplaninnovations.looprwallet.fragments.signin

import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.adapters.phrase.PhraseAdapter
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.longToast
import kotlinx.android.synthetic.main.fragment_enter_phrase_confirm.*
import android.support.v7.widget.helper.ItemTouchHelper
import com.caplaninnovations.looprwallet.adapters.OnStartDragListener
import com.caplaninnovations.looprwallet.handlers.SimpleItemTouchHandler


/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class WalletEnterPhraseFragment : BaseFragment(), OnStartDragListener {

    companion object {
        val TAG: String = WalletEnterPhraseFragment::class.java.simpleName

        private const val KEY_PHRASE = "_PHRASE"

        const val KEY_FRAGMENT_TYPE = "_FRAGMENT_TYPE"
        const val TYPE_CONFIRM_PHRASE = "TYPE_CONFIRM_PHRASE"
        const val TYPE_ENTER_PHRASE = "TYPE_ENTER_PHRASE"

        private const val PHRASE_SIZE = 12

        /**
         * @param type Can be one of [TYPE_CONFIRM_PHRASE] or [TYPE_ENTER_PHRASE]
         */
        fun createInstance(type: String): WalletEnterPhraseFragment {
            return WalletEnterPhraseFragment().apply {
                arguments = Bundle().apply { putString(KEY_FRAGMENT_TYPE, type) }
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_enter_phrase_confirm

    private lateinit var phrase: ArrayList<String>

    private val fragmentType by lazy {
        arguments!!.getString(KEY_FRAGMENT_TYPE)
    }

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phrase = savedInstanceState?.getStringArrayList(KEY_PHRASE) ?: arrayListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        enterPhraseAddSubmitButton.setOnClickListener { onEnterPhraseAddSubmitButtonClick() }

        enterPhraseRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = PhraseAdapter(phrase, this::onWordRemoved, this)
        enterPhraseRecyclerView.adapter = adapter

        itemTouchHelper = ItemTouchHelper(SimpleItemTouchHandler(adapter))
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    /**
     * Adds the given word to the phrase list and propagates changes to the UI (if necessary) and
     * recycler adapter
     */
    fun onWordAdded(word: String) {
        phrase.add(word)

        enterPhraseRecyclerView.adapter.notifyItemInserted(phrase.size - 1)

        enterPhraseRecyclerView.smoothScrollToPosition(phrase.size - 1)

        if (phrase.size == PHRASE_SIZE) {
            // We need to disallow more words from being inputted and allow the user to submit
            TransitionManager.beginDelayedTransition(view as ViewGroup)
            enterPhraseAddSubmitButton.text = getEnterPhraseSubmitButtonText()
            enterPhraseAddSubmitButton.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

            enterPhraseInputLayout.visibility = View.GONE
        }
    }

    /**
     * Removes the given word from the phrase list and propagates changes to the UI (if necessary)
     * and recycler adapter
     */
    fun onWordRemoved(word: String) {
        phrase.remove(word)

        enterPhraseRecyclerView.adapter.notifyItemRemoved(phrase.size)

        if (phrase.size == PHRASE_SIZE - 1) {
            // We are downsizing from 12. Allow the user to input new words
            (view as? ViewGroup)?.let { TransitionManager.beginDelayedTransition(it) }

            enterPhraseAddSubmitButton.apply {
                text = getString(R.string.add)
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                requestLayout()
            }

            enterPhraseInputLayout.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putStringArrayList(KEY_PHRASE, phrase)
    }

    // MARK - Private Methods

    private fun onEnterPhraseAddSubmitButtonClick() {
        val enteredWord = enterPhraseEditText.text.toString().trim().toLowerCase()

        when {
            enteredWord.isEmpty() || !enteredWord.matches(Regex("[a-z]+")) -> {
                context?.longToast(R.string.error_enter_valid_word)
            }
            else -> {
                if (phrase.size == PHRASE_SIZE) {
                    // TODO
                    Toast.makeText(context, "Creating...", Toast.LENGTH_LONG).show()
                } else {
                    onWordAdded(enteredWord)
                }
            }
        }
    }

    private fun getEnterPhraseSubmitButtonText(): String {
        return when (fragmentType) {
            TYPE_CONFIRM_PHRASE -> getString(R.string.create_wallet)
            TYPE_ENTER_PHRASE -> getString(R.string.restore_wallet)
            else -> throw IllegalArgumentException("Invalid fragmentType, found $fragmentType")
        }
    }

}