package com.caplaninnovations.looprwallet.fragments.signin

import android.os.Bundle
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.utilities.longToast
import kotlinx.android.synthetic.main.fragment_enter_phrase_confirm.*

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

        const val KEY_FRAGMENT_TYPE = "_FRAGMENT_TYPE"
        const val TYPE_CONFIRM_PHRASE = "CONFIRM_PHRASE"
        const val TYPE_ENTER_PHRASE = "ENTER_PHRASE"

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

    private val phrase = mutableListOf<String>()

    private val fragmentType by lazy {
        arguments!!.getString(KEY_FRAGMENT_TYPE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterPhraseAddSubmitButton.setOnClickListener {
            val enteredWord = enterPhraseEditText.text.toString().trim().toLowerCase()

            when {
                enteredWord.isEmpty() || !enteredWord.matches(Regex("[a-z]+")) -> {
                    context?.longToast(R.string.error_enter_valid_word)
                }
                else -> {
                    if (phrase.size == 12) {
                        Toast.makeText(context, "Creating...", Toast.LENGTH_LONG).show()
                    } else {
                        onWordAdded(enteredWord)
                    }
                }
            }
        }
    }

    /**
     * Adds the given word to the phrase list and propagates changes to the UI (if necessary) and
     * recycler adapter
     */
    fun onWordAdded(word: String) {
        phrase.add(word)

        if (phrase.size == 12) {
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

        if (phrase.size == 12) {
            // We are downsizing from 12. Allow the user to input new words and disable submission
            TransitionManager.beginDelayedTransition(view as ViewGroup)
            enterPhraseAddSubmitButton.text = getString(R.string.add)
            enterPhraseAddSubmitButton.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            enterPhraseAddSubmitButton.requestLayout()

            enterPhraseInputLayout.visibility = View.VISIBLE
        }
    }

    // MARK - Private Methods

    fun getEnterPhraseSubmitButtonText(): String {
        return when (fragmentType) {
            TYPE_CONFIRM_PHRASE -> getString(R.string.create_wallet)
            TYPE_ENTER_PHRASE -> getString(R.string.restore_wallet)
            else -> throw IllegalArgumentException("Invalid fragmentType, found $fragmentType")
        }
    }

}