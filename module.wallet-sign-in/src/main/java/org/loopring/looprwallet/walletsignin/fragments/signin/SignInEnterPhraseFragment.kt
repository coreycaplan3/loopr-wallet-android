package org.loopring.looprwallet.walletsignin.fragments.signin

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.os.bundleOf
import kotlinx.android.synthetic.main.fragment_enter_phrase_confirm.*
import org.loopring.looprwallet.core.adapters.OnStartDragListener
import org.loopring.looprwallet.core.extensions.allEqual
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.delegates.SimpleItemTouchDelegate
import org.loopring.looprwallet.core.utilities.RegexUtility
import org.loopring.looprwallet.walletsignin.R
import org.loopring.looprwallet.walletsignin.adapters.phrase.PhraseAdapter
import org.loopring.looprwallet.walletsignin.models.wallet.WalletCreationPhrase
import org.loopring.looprwallet.walletsignin.viewmodels.WalletGeneratorViewModel

/**
 * Created by Corey on 2/22/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class SignInEnterPhraseFragment : BaseFragment() {

    companion object {
        val TAG: String = SignInEnterPhraseFragment::class.java.simpleName

        private const val KEY_PHRASE = "_PHRASE"
        private const val KEY_CORRECT_PHRASE = "_CORRECT_PHRASE"
        private const val KEY_WALLET_CREATION = "_WALLET_CREATION"

        private const val KEY_FRAGMENT_TYPE = "_FRAGMENT_TYPE"
        private const val TYPE_CONFIRM_PHRASE = "TYPE_CONFIRM_PHRASE"
        private const val TYPE_ENTER_PHRASE = "TYPE_ENTER_PHRASE"

        private const val PHRASE_SIZE = 12

        /**
         * Creates a fragment for confirming a phrase (usually for creation purposes to verify that
         * the user wrote down his/her phrase)
         *
         * @param wallet The wallet object containing the information for creating the phrase.
         * Should have the *phrase* field set
         */
        fun createConfirmPhraseInstance(wallet: WalletCreationPhrase): SignInEnterPhraseFragment {
            return SignInEnterPhraseFragment().apply {
                arguments = setupArguments(TYPE_CONFIRM_PHRASE, wallet, wallet.phrase)
            }
        }

        /**
         * Creates a fragment for entering a phrase (usually for recovery purposes)
         */
        fun createEnterPhraseInstance(wallet: WalletCreationPhrase): SignInEnterPhraseFragment {
            return SignInEnterPhraseFragment().apply {
                arguments = setupArguments(TYPE_ENTER_PHRASE, wallet, null)
            }
        }

        private fun setupArguments(fragmentType: String,
                                   walletCreationPhrase: WalletCreationPhrase,
                                   correctPhrase: ArrayList<String>?) =
                bundleOf(
                        KEY_FRAGMENT_TYPE to fragmentType,
                        KEY_WALLET_CREATION to walletCreationPhrase,
                        KEY_CORRECT_PHRASE to correctPhrase
                )
    }

    override val layoutResource: Int
        get() = R.layout.fragment_enter_phrase_confirm

    lateinit var phrase: ArrayList<String>

    private val correctPhrase: ArrayList<String> by lazy {
        arguments!!.getStringArrayList(KEY_CORRECT_PHRASE)
    }

    private val walletCreationPhrase: WalletCreationPhrase by lazy {
        arguments!!.getParcelable(KEY_WALLET_CREATION) as WalletCreationPhrase
    }

    private val fragmentType by lazy {
        arguments!!.getString(KEY_FRAGMENT_TYPE)
    }

    val walletGeneratorViewModel: WalletGeneratorViewModel by lazy {
        ViewModelProviders.of(this).get(WalletGeneratorViewModel::class.java)
    }

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phrase = savedInstanceState?.getStringArrayList(KEY_PHRASE) ?: arrayListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterPhraseAddSubmitButton.setOnClickListener { onSubmitButtonClick() }

        enterPhraseEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> enterPhraseAddSubmitButton.performClick()
                else -> false
            }
        }

        setupFormUI()

        fragmentContainer.apply {
            this.layoutManager = LinearLayoutManager(context)

            val adapter = PhraseAdapter(
                    phrase,
                    this@SignInEnterPhraseFragment::onWordRemoved,
                    object : OnStartDragListener {
                        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                            itemTouchHelper?.startDrag(viewHolder)
                        }
                    }
            )
            this.adapter = adapter

            itemTouchHelper = ItemTouchHelper(SimpleItemTouchDelegate(adapter))
            itemTouchHelper?.attachToRecyclerView(this)
        }

        enableToolbarCollapsing()

        WalletGeneratorViewModel.setupForFragment(walletGeneratorViewModel, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putStringArrayList(KEY_PHRASE, phrase)
    }

    // MARK - Private Methods

    private fun onSubmitButtonClick() {
        val enteredWord = enterPhraseEditText.text.toString().trim().toLowerCase()

        when {
            phrase.size == PHRASE_SIZE -> {
                submitPhrase()
            }
            enteredWord.isEmpty() || !enteredWord.matches(RegexUtility.LETTERS_REGEX) -> {
                context?.longToast(R.string.error_phrase_enter_valid_word)
            }
            else -> {
                onWordAdded(enteredWord)
                enterPhraseEditText.text = null
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
        appbarLayout?.setExpanded(false)

        if (phrase.size == PHRASE_SIZE) {
            // We need to disallow more words from being inputted and allow the user to submit
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(view?.windowToken, 0)

            TransitionManager.beginDelayedTransition(view as ViewGroup)
            setupFormUI()
        }
    }

    private fun onWordRemoved() {
        if (phrase.size == PHRASE_SIZE - 1) {
            // We are downsizing from 12. Allow the user to input new words
            (view as? ViewGroup)?.let { TransitionManager.beginDelayedTransition(it) }
            setupFormUI()
        }
    }

    private fun submitPhrase() {
        val walletName = walletCreationPhrase.walletName
        val password = walletCreationPhrase.password

        when (fragmentType) {
            TYPE_CONFIRM_PHRASE -> {
                if (phrase.allEqual(correctPhrase) { one, two -> one == two }) {
                    walletGeneratorViewModel.loadPhraseWallet(walletName, password, phrase)
                } else {
                    context?.longToast(R.string.error_incorrect_phrase)
                }
            }
            TYPE_ENTER_PHRASE -> {
                walletGeneratorViewModel.loadPhraseWallet(walletName, password, phrase)
            }
            else -> throw IllegalStateException("Invalid type, found: $fragmentType")
        }
    }

    private fun setupFormUI() {
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

    private fun getEnterPhraseSubmitButtonText(): String {
        return when (fragmentType) {
            TYPE_CONFIRM_PHRASE -> getString(R.string.create_wallet)
            TYPE_ENTER_PHRASE -> getString(R.string.restore_wallet)
            else -> throw IllegalArgumentException("Invalid fragmentType, found $fragmentType")
        }
    }

}