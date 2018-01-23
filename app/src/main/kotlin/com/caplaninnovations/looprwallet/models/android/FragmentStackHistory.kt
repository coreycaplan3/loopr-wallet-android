package com.caplaninnovations.looprwallet.models.android

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.caplaninnovations.looprwallet.utilities.logv
import java.util.*

/**
 * Created by Corey on 1/19/2018.
 * Project: LooprWallet
 * <p></p>
 * Purpose of Class:
 */
class FragmentStackHistory : ViewModel() {

    private val stack: Stack<String> = Stack()

    val currentFragment: MutableLiveData<String> = MutableLiveData()

    /**
     * Pushes the given fragment (represented via Int) onto the stack. If it is already on the
     * stack, it is moved to the front.
     */
    fun push(fragmentTag: String) {
        if (!isInStack(fragmentTag)) {
            logv("Putting $fragmentTag in the front of the  stack")
            stack.push(fragmentTag)
        } else {
            // Remove it, and push it to the front
            logv("Moving $fragmentTag to the front of the stack")
            stack.remove(fragmentTag)
            stack.push(fragmentTag)
        }

        currentFragment.value = fragmentTag
    }

    /**
     * Pops an entry off the stack, and returns the String represented by the fragment. If the stack
     * is empty, null is returned
     */
    fun pop(): String? {
        if (stack.isEmpty()) return null

        val poppedFragment = stack.pop()
        currentFragment.value = peek()

        return poppedFragment
    }

    fun remove(fragmentTag: String) {
        val currentFragmentTag = peek()

        if (fragmentTag.equals(currentFragmentTag)) pop() // we're changing the top of the stack
        else stack.remove(fragmentTag) // we're not changing the UI directly, just removing something
    }

    /**
     * True if the stack is empty, false otherwise
     */
    fun isEmpty() = stack.isEmpty()

    // MARK - Private Methods

    /**
     * Peeks the first entry on the stack, and returns the String represented by the fragment. If
     * the stack is empty, null is returned
     */
    private fun peek(): String? = if (stack.isEmpty()) null else stack.peek()

    private fun isInStack(fragment: String): Boolean = stack.contains(fragment)

}