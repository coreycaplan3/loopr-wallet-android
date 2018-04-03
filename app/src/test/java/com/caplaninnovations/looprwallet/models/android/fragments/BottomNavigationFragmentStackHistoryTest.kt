package com.caplaninnovations.looprwallet.models.android.fragments

import android.os.Bundle
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.loopring.looprwallet.core.models.android.fragments.BottomNavigationFragmentStackHistory
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Corey on 2/6/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
@RunWith(MockitoJUnitRunner::class)
class BottomNavigationFragmentStackHistoryTest {

    private val fragmentStackHistory = BottomNavigationFragmentStackHistory(true, null)

    private val tagOne = "one"
    private val tagTwo = "two"
    private val tagThree = "three"

    @Test
    fun pushToFront() {
        fragmentStackHistory.push(tagOne)
        fragmentStackHistory.push(tagTwo)
        fragmentStackHistory.push(tagThree)
        fragmentStackHistory.push(tagOne)

        assertEquals(3, fragmentStackHistory.getStackSize())

        assertEquals(tagOne, fragmentStackHistory.pop())
        assertEquals(tagThree, fragmentStackHistory.pop())
        assertEquals(tagTwo, fragmentStackHistory.pop())

        assertTrue(fragmentStackHistory.isEmpty())
    }

    @Test
    fun remove() {
        fragmentStackHistory.push(tagOne)
        fragmentStackHistory.push(tagTwo)
        fragmentStackHistory.push(tagThree)
        fragmentStackHistory.push(tagOne)

        assertTrue(fragmentStackHistory.remove(tagTwo))

        assertEquals(2, fragmentStackHistory.getStackSize())

        assertEquals(tagOne, fragmentStackHistory.peek())
    }

    @Test
    fun peek() {
        fragmentStackHistory.push(tagOne)
        fragmentStackHistory.push(tagTwo)
        fragmentStackHistory.push(tagThree)
        fragmentStackHistory.push(tagOne)

        assertEquals(tagOne, fragmentStackHistory.peek())
        assertEquals(3, fragmentStackHistory.getStackSize())
    }

    @Test
    fun isEmpty() {
        assertTrue(fragmentStackHistory.isEmpty())

        fragmentStackHistory.push(tagOne)
        assertFalse(fragmentStackHistory.isEmpty())

        val poppedTag = fragmentStackHistory.pop()
        assertEquals(tagOne, poppedTag)

        assertTrue(fragmentStackHistory.isEmpty())
    }

    @Test
    fun saveState() {
        val bundle = Mockito.mock(Bundle::class.java)

        fragmentStackHistory.push(tagOne)
        fragmentStackHistory.push(tagTwo)
        fragmentStackHistory.push(tagThree)

        Mockito.`when`(bundle.getStringArrayList(BottomNavigationFragmentStackHistory.KEY_STACK))
                .thenReturn(fragmentStackHistory.stack)

        fragmentStackHistory.saveState(bundle)

        val newStack = BottomNavigationFragmentStackHistory(false, bundle)

        assertEquals(3, newStack.getStackSize())

        assertEquals(tagThree, newStack.pop())
        assertEquals(tagTwo, newStack.pop())
        assertEquals(tagOne, newStack.pop())

        assertNull(newStack.pop())
        assertTrue(newStack.isEmpty())
    }

    @Test
    fun getStackSize() {
        assertEquals(0, fragmentStackHistory.getStackSize())

        fragmentStackHistory.push(tagOne)
        assertEquals(1, fragmentStackHistory.getStackSize())

        fragmentStackHistory.push(tagTwo)
        assertEquals(2, fragmentStackHistory.getStackSize())

        fragmentStackHistory.push(tagThree)
        assertEquals(3, fragmentStackHistory.getStackSize())

        fragmentStackHistory.push(tagThree)
        assertEquals(3, fragmentStackHistory.getStackSize())

        fragmentStackHistory.push(tagOne)
        assertEquals(3, fragmentStackHistory.getStackSize())
    }

    @Test
    fun isUpNavigationEnabled() {
        // There are not enough items in the stack.
        assertFalse(fragmentStackHistory.isUpNavigationEnabled())

        fragmentStackHistory.push(tagOne)

        // There are not enough items in the stack.
        assertFalse(fragmentStackHistory.isUpNavigationEnabled())

        fragmentStackHistory.push(tagTwo)
        assertTrue(fragmentStackHistory.isUpNavigationEnabled())

        val fragmentStackHistory = BottomNavigationFragmentStackHistory(false, null)
        assertFalse(fragmentStackHistory.isUpNavigationEnabled())

        fragmentStackHistory.push(tagOne)

        assertFalse(fragmentStackHistory.isUpNavigationEnabled())

        fragmentStackHistory.push(tagTwo)
        assertFalse(fragmentStackHistory.isUpNavigationEnabled())
    }

}