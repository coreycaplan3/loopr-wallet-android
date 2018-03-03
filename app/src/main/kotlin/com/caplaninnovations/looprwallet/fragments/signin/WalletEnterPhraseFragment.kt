package com.caplaninnovations.looprwallet.fragments.signin

import android.content.Context
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
import android.view.WindowManager.LayoutParams.*
import android.view.inputmethod.EditorInfo
import com.caplaninnovations.looprwallet.adapters.OnStartDragListener
import com.caplaninnovations.looprwallet.handlers.SimpleItemTouchHandler
import com.caplaninnovations.looprwallet.utilities.logd
import kotlin.collections.ArrayList
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout


/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class WalletEnterPhraseFragment : BaseFragment() {

    companion object {
        val TAG: String = WalletEnterPhraseFragment::class.java.simpleName

        private const val KEY_PHRASE = "_PHRASE"

        const val KEY_FRAGMENT_TYPE = "_FRAGMENT_TYPE"
        private const val TYPE_CONFIRM_PHRASE = "TYPE_CONFIRM_PHRASE"
        private const val TYPE_ENTER_PHRASE = "TYPE_ENTER_PHRASE"

        private const val PHRASE_SIZE = 12

        /**
         * Creates a fragment for confirming a phrase (usually for creation purposes to verify that
         * the user wrote down his/her phrase)
         *
         * @param phrase The correct phrase. Used for checking against what the user entered.
         */
        fun createConfirmPhraseInstance(phrase: ArrayList<String>): WalletEnterPhraseFragment {
            return WalletEnterPhraseFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_FRAGMENT_TYPE, TYPE_ENTER_PHRASE)
                    putStringArrayList(KEY_PHRASE, phrase)
                }
            }
        }

        /**
         * Creates a fragment for entering a phrase (usually for recovery purposes)
         */
        fun createEnterPhraseInstance(): WalletEnterPhraseFragment {
            return WalletEnterPhraseFragment().apply {
                arguments = Bundle().apply { putString(KEY_FRAGMENT_TYPE, TYPE_ENTER_PHRASE) }
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_enter_phrase_confirm

    private lateinit var phrase: ArrayList<String>

    private val correctPhrase: ArrayList<String> by lazy {
        arguments?.getStringArrayList(KEY_PHRASE)
                ?: throw IllegalStateException("Arguments should not be null!")
    }

    private val fragmentType by lazy {
        arguments!!.getString(KEY_FRAGMENT_TYPE)
    }

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phrase = savedInstanceState?.getStringArrayList(KEY_PHRASE) ?: arrayListOf("hello", "there", "wallet", "banana", "apple", "pear", "melon", "berry", "pineapple", "tree")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)

        enterPhraseAddSubmitButton.setOnClickListener { onEnterPhraseAddSubmitButtonClick() }

        enterPhraseEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> enterPhraseAddSubmitButton.performClick()
                else -> false
            }
        }

        setupForm()

        fragmentContainer.apply {
            this.layoutManager = LinearLayoutManager(context)

            val adapter = PhraseAdapter(
                    phrase,
                    this@WalletEnterPhraseFragment::onWordRemoved,
                    object : OnStartDragListener {
                        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                            itemTouchHelper?.startDrag(viewHolder)
                        }
                    }
            )
            this.adapter = adapter

            itemTouchHelper = ItemTouchHelper(SimpleItemTouchHandler(adapter))
            itemTouchHelper?.attachToRecyclerView(this)
        }

        enableToolbarCollapsing()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
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
                    enterPhraseEditText.text = null
                }
            }
        }
    }

    /**
     * Adds the given word to the phrase list and propagates changes to the UI (if necessary) and
     * recycler adapter
     */
    private fun onWordAdded(word: String) {
        logd("Adding word: $word")
        (fragmentContainer.adapter as PhraseAdapter).onWordAdded(word)
        fragmentContainer.smoothScrollToPosition(phrase.size - 1)

        if (phrase.size == PHRASE_SIZE) {
            // We need to disallow more words from being inputted and allow the user to submit
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(view?.windowToken, 0)

            TransitionManager.beginDelayedTransition(view as ViewGroup)
            setupForm()
        }
    }

    private fun onWordRemoved() {
        if (phrase.size == PHRASE_SIZE - 1) {
            // We are downsizing from 12. Allow the user to input new words
            (view as? ViewGroup)?.let { TransitionManager.beginDelayedTransition(it) }
            setupForm()
        }
    }

    private fun getEnterPhraseSubmitButtonText(): String {
        return when (fragmentType) {
            TYPE_CONFIRM_PHRASE -> getString(R.string.create_wallet)
            TYPE_ENTER_PHRASE -> getString(R.string.restore_wallet)
            else -> throw IllegalArgumentException("Invalid fragmentType, found $fragmentType")
        }
    }

    private fun setupForm() {
        if (phrase.size == PHRASE_SIZE) {
            enterPhraseAddSubmitButton.text = getEnterPhraseSubmitButtonText()
            (enterPhraseInputLayout.layoutParams as? LinearLayout.LayoutParams)?.let {
                it.weight = 0F
                enterPhraseInputLayout.requestLayout()
            }
        } else {
            enterPhraseAddSubmitButton.text = getString(R.string.add)
            (enterPhraseInputLayout.layoutParams as? LinearLayout.LayoutParams)?.let {
                it.weight = 0.75F
                enterPhraseInputLayout.requestLayout()
            }
        }
    }

}