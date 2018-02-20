package com.caplaninnovations.looprwallet.models.android.fragments

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.caplaninnovations.looprwallet.utilities.logv
import kotlin.collections.ArrayList

/**
 * Created by Corey on 1/19/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 * @param isUpNavigationEnabled True if upwards navigation should be enabled or false to disabled.
 * This is mainly useful for bottom-navigation, when the user is navigating "horizontally" instead
 * of vertically.
 * @param savedInstanceState The bundle passed to the activity in its *onCreate*.
 */
class FragmentStackHistory(private val isUpNavigationEnabled: Boolean, savedInstanceState: Bundle?) {

    companion object {
        const val KEY_STACK = "_STACK"
    }

    @VisibleForTesting
    val stack: ArrayList<String>

    init {
        stack = savedInstanceState?.getStringArrayList(KEY_STACK) ?: ArrayList()
    }

    /**
     * Pushes the given fragment (represented via tag) onto the stack. If it is already on the
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

    /**
     * @return True if the tag was removed or false if it was not found in the stack
     */
    fun remove(fragmentTag: String): Boolean {
        val currentFragmentTag = peek()

        return if (fragmentTag == currentFragmentTag) {
            pop() // We're changing the top of the stack
            true
        } else {
            stack.remove(fragmentTag) // we're not changing the UI directly, just removing something
        }
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

    /**
     * @return True to display and support up navigation or false to disable and hide it
     */
    fun isUpNavigationEnabled(): Boolean = isUpNavigationEnabled && stack.size > 1

    fun saveState(outState: Bundle?) {
        outState?.putStringArrayList(KEY_STACK, stack)
    }

    // MARK - Private Methods

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getStackSize() = stack.size

    private fun isInStack(fragment: String): Boolean = stack.contains(fragment)

}