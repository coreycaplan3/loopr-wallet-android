package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.view.View
import android.widget.TextView

/**
 * Created by Corey Caplan on 2/1/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class TopPaddingAssertion(@DimenRes private val dimensionResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException) {
        val expectedPadding = view?.context?.resources?.getDimension(dimensionResource)?.toInt()
        if (view?.paddingTop != expectedPadding) {
            throw IllegalStateException("Invalid padding! " +
                    "Expected $expectedPadding, but found ${view?.paddingTop}")
        }
    }

}

class HeightAssertion(@DimenRes private val heightResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException) {
        val expectedHeight = view?.context?.resources?.getDimension(heightResource)?.toInt()
        if (view?.layoutParams?.height != expectedHeight) {
            throw IllegalStateException("Invalid height! " +
                    "Expected $expectedHeight, but found ${view?.height}")
        }
    }

}

class AlphaAssertion(private val alpha: Float) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException) {
        if (view?.alpha != alpha) {
            throw IllegalStateException("Invalid alpha! " +
                    "Expected $alpha, but found ${view?.alpha}")
        }
    }

}

class TextSizeAssertion(@DimenRes private val textSizeResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException) {
        val expectedTextSize = view?.context?.resources?.getDimension(textSizeResource)
        val foundTextSize = (view as? TextView)?.textSize
        if (foundTextSize != expectedTextSize) {
            throw IllegalStateException("Invalid text size! " +
                    "Expected $expectedTextSize, but found $foundTextSize")
        }
    }

}