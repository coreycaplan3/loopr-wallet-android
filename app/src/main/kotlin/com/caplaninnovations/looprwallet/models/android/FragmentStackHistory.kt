package com.caplaninnovations.looprwallet.models.android

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import com.caplaninnovations.looprwallet.fragments.MarketsParentFragment
import com.caplaninnovations.looprwallet.fragments.MyWalletFragment
import com.caplaninnovations.looprwallet.fragments.OrdersParentFragment
import com.caplaninnovations.looprwallet.utilities.logv
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Corey on 1/19/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class FragmentStackHistory(savedInstanceState: Bundle?) {

    private val tagStack = "_Stack"

    private val stack: ArrayList<String>

    init {
        stack = savedInstanceState?.getStringArrayList(tagStack) ?: ArrayList()
    }

    /**
     * Pushes the given fragment (represented via Int) onto the stack. If it is already on the
     * stack, it is moved to the front.
     */
    fun push(fragmentTag: String) {
        if (!isInStack(fragmentTag)) {
            logv("Putting $fragmentTag in the front of the  stack")
            stack.add(0, fragmentTag)
        } else {
            // Remove it, and push it to the front
            logv("Moving $fragmentTag to the front of the stack")
            stack.remove(fragmentTag)
            stack.add(0, fragmentTag)
        }
    }

    /**
     * Pops an entry off the stack, and returns the String represented by the fragment. If the stack
     * is empty, null is returned
     */
    fun pop(): String? {
        if (stack.isEmpty()) return null

        return stack.removeAt(0)
    }

    fun remove(fragmentTag: String) {
        val currentFragmentTag = peek()

        if (fragmentTag.equals(currentFragmentTag)) pop() // we're changing the top of the stack
        else stack.remove(fragmentTag) // we're not changing the UI directly, just removing something
    }

    /**
     * Peeks the first entry on the stack, and returns the String represented by the fragment. If
     * the stack is empty, null is returned
     */
    fun peek(): String? = if (stack.isEmpty()) null else stack[0]

    /**
     * True if the stack is empty, false otherwise
     */
    fun isEmpty() = stack.isEmpty()

    fun saveState(outState: Bundle?) {
        outState?.putStringArrayList(tagStack, stack)
    }

    // MARK - Private Methods

    private fun isInStack(fragment: String): Boolean = stack.contains(fragment)

}