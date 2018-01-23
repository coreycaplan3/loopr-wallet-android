package com.caplaninnovations.looprwallet.models.android

import android.os.Bundle
import com.caplaninnovations.looprwallet.utilities.logv
import kotlin.collections.ArrayList

/**
 * Created by Corey on 1/19/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class FragmentStackHistory(savedInstanceState: Bundle?) {

    private val tagStack = "FragmentStackHistory_stack"
    private val stack: ArrayList<String>

    lateinit var currentTab: String

    init {
        @Suppress("UNCHECKED_CAST")
        stack = (savedInstanceState?.get(tagStack) as? ArrayList<String>) ?: ArrayList<String>()

        if(stack.isNotEmpty()) {
            currentTab = stack[0]
        }
    }

    /**
     * Pushes the given fragment (represented via Int) onto the stack. If it is already on the
     * stack, it is moved to the front.
     */
    fun push(fragmentTag: String) {
        if(isEmpty()) {
            currentTab = fragmentTag
        }

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

        val poppedFragment = stack.removeAt(0)

        return poppedFragment
    }

    /**
     * True if the stack is empty, false otherwise
     */
    fun isEmpty() = stack.isEmpty()

    /**
     * Peeks the first entry on the stack, and returns the String represented by the fragment. If
     * the stack is empty, null is returned
     */
    fun peek(): String? = if (stack.isEmpty()) null else stack[0]

    fun saveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putStringArrayList(tagStack, stack)
    }

    // MARK - Private Methods

    private fun isInStack(fragment: String): Boolean = stack.contains(fragment)

}