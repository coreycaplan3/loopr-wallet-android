package com.caplaninnovations.looprwallet.fragments.signin

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.contrib.RecyclerViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.adapters.phrase.PhraseEmptyViewHolder
import com.caplaninnovations.looprwallet.adapters.phrase.PhraseViewHolder
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.wallet.creation.WalletCreationPhrase
import org.loopring.looprwallet.core.utilities.CustomViewActions.dragDownViewHolder
import org.loopring.looprwallet.core.utilities.CustomViewActions.dragUpViewHolder
import kotlinx.android.synthetic.main.fragment_enter_phrase_confirm.*
import kotlinx.android.synthetic.main.view_holder_phrase.*
import kotlinx.android.synthetic.main.view_holder_phrase.view.*
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.Espresso.onView
import org.junit.After
import java.util.concurrent.FutureTask


/**
 * Created by Corey on 3/6/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SignInEnterPhraseFragmentTest : BaseDaggerFragmentTest<SignInEnterPhraseFragment>() {

    private val correctPhrase = arrayListOf(
            "outer", "scissors", "atom", "legend", "involve", "common", "school", "smoke", "write",
            "pizza", "breeze", "raise"
    )

    private val walletForCreation = WalletCreationPhrase("looprCreation", "looprwallet1", correctPhrase)
    private val walletForRestoration = WalletCreationPhrase("looprRestoration", "looprwallet2", correctPhrase)

    override var fragment = SignInEnterPhraseFragment.createConfirmPhraseInstance(walletForCreation)
    override val tag = SignInEnterPhraseFragment.TAG

    @Before
    fun setup() {
        assertEquals(12, correctPhrase.size)
    }

    @After
    fun tearDown() {
        walletClient.removeWallet(walletForCreation.walletName)
        walletClient.removeWallet(walletForRestoration.walletName)
    }

    @Test
    fun initialStateEmpty() {
        // There's the empty-state text
        assertEquals(1, fragment.fragmentContainer.adapter.itemCount)
        assertEquals(0, fragment.phrase.size)

        val viewHolder = fragment.fragmentContainer.findViewHolderForAdapterPosition(0)
        assertTrue(viewHolder is PhraseEmptyViewHolder)
    }

    @Test
    fun addWord_fromEmptyState() {
        addWord(correctPhrase[0])

        val viewHolder = fragment.fragmentContainer.findViewHolderForAdapterPosition(0)
        assertTrue(viewHolder is PhraseViewHolder)
    }

    @Test
    fun reorderWord_phrasePositionsChange() {
        for (i in 0 until 5) {
            addWord(correctPhrase[i])
        }

        assertEquals(5, fragment.phrase.size)
        assertEquals(5, fragment.fragmentContainer.adapter.itemCount)

        val thirdViewHolder = getViewHolderForPosition<PhraseViewHolder>(2)

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(2))

        Espresso.onView(`is`(thirdViewHolder.phraseReorderButton))
                .perform(dragUpViewHolder(thirdViewHolder.itemView, 1))

        assertEquals(fragment.phrase[2], correctPhrase[1])

        // Move the first ViewHolder to the 2nd position (first index)
        val firstViewHolder = getViewHolderForPosition<PhraseViewHolder>(0)

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(0))

        Espresso.onView(`is`(firstViewHolder.phraseReorderButton))
                .perform(dragDownViewHolder(thirdViewHolder.itemView, 1))

        // The correct phrase at the 2nd index was moved down twice
        assertEquals(fragment.phrase[0], correctPhrase[2])
    }

    @Test
    fun removeWord_phraseEmpty() {
        for (i in 0 until 3) {
            addWord(correctPhrase[i])
        }

        for (i in 0 until 3) {
            val holder = getViewHolderForPosition<PhraseViewHolder>(0)
            Espresso.onView(`is`(holder.itemView))
                    .perform(swipeRight())

            assertEquals(2 - i, fragment.phrase.size)
        }

        assertEquals(0, fragment.phrase.size)

        assertEquals(1, fragment.fragmentContainer.adapter.itemCount)

        val holder = getViewHolderForPosition<PhraseEmptyViewHolder>(0)
        assertNotNull(holder)
    }

    @Test
    fun removeWord_phraseNotEmpty() {
        for (i in 0 until 3) {
            addWord(correctPhrase[i])
        }

        val holder0 = getViewHolderForPosition<PhraseViewHolder>(0)
        Espresso.onView(`is`(holder0.itemView))
                .perform(swipeRight())

        assertEquals(2, fragment.phrase.size)
        assertEquals(2, fragment.fragmentContainer.adapter.itemCount)

        for (i in 0 until fragment.fragmentContainer.adapter.itemCount) {
            val holder = getViewHolderForPosition<PhraseViewHolder>(i)
            val expectedText = "${i + 1} - ${correctPhrase[i + 1]}"
            assertEquals(expectedText, holder.phraseWordLabel.text)
        }
    }

    @Test
    fun phraseOkay_createWallet() {
        for (i in 0 until correctPhrase.size) {
            addWord(correctPhrase[i])
        }

        waitForAnimationsAndScrollToEnd()

        Espresso.onView(`is`(fragment.enterPhraseAddSubmitButton))
                .check(matches(withText(R.string.create_wallet)))
                .perform(click())

        assertActivityActive(MainActivity::class.java)
    }

    @Test
    fun phraseBad_createWallet() {
        for (i in 0 until correctPhrase.size - 1) {
            addWord(correctPhrase[i])
        }

        addWord("incorrect")

        waitForAnimationsAndScrollToEnd()

        Espresso.onView(`is`(fragment.enterPhraseAddSubmitButton))
                .check(matches(withText(R.string.create_wallet)))
                .perform(click())

        onView(withText(R.string.error_incorrect_phrase))
                .inRoot(withDecorView(not(`is`(activity.window.decorView))))
                .check(matches(isDisplayed()))
    }

    @Test
    fun phraseOkay_restoreWallet() {
        fragment = SignInEnterPhraseFragment.createEnterPhraseInstance(walletForRestoration)
        val task = FutureTask { activity.addFragment(fragment, SignInEnterPhraseFragment.TAG) }
        waitForTask(activity, task, true)

        waitForActivityToBeSetup()

        for (i in 0 until correctPhrase.size) {
            addWord(correctPhrase[i])
        }

        waitForAnimationsAndScrollToEnd()

        Espresso.onView(`is`(fragment.enterPhraseAddSubmitButton))
                .check(matches(withText(R.string.restore_wallet)))
                .perform(click())

        assertActivityActive(MainActivity::class.java)
    }

    private fun addWord(word: String) {
        val expectedPhraseSize = fragment.phrase.size + 1
        Espresso.onView(`is`(fragment.enterPhraseEditText))
                .perform(typeText(word), closeSoftKeyboard())
                .check(matches(withText(word)))

        Espresso.onView(`is`(fragment.enterPhraseAddSubmitButton))
                .perform(click())

        Espresso.onView(`is`(fragment.enterPhraseEditText))
                .check(matches(withText("")))

        assertEquals(expectedPhraseSize, fragment.phrase.size)
        assertEquals(expectedPhraseSize, fragment.fragmentContainer.adapter.itemCount)

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(fragment.phrase.size - 1))

        val addedViewHolder = getViewHolderForPosition<PhraseViewHolder>(fragment.phrase.size - 1)
        val expectedText = "${addedViewHolder.adapterPosition + 1} - $word"

        Espresso.onView(`is`(addedViewHolder.itemView.phraseWordLabel))
                .check(matches(withText(expectedText)))
    }

    private fun <T : RecyclerView.ViewHolder> getViewHolderForPosition(position: Int): T {
        @Suppress("UNCHECKED_CAST")
        val holder = fragment.fragmentContainer.findViewHolderForAdapterPosition(position) as T
        assertNotNull(holder)
        assertEquals(position, holder.adapterPosition)
        return holder
    }

    /**
     * Waits 300ms and then scrolls to the end of the RecyclerView
     */
    private fun waitForAnimationsAndScrollToEnd() {
        // Wait for animations to propagate
        Thread.sleep(300)

        Espresso.onView(`is`(fragment.fragmentContainer))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(fragment.phrase.size - 1))
    }

}