package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.view.View
import android.widget.TextView

/**
 * Created by Corey on 2/5/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To easily make [Espresso] assertions about a [TextView]'s text size
 *
 */
class TextSizeAssertion(@DimenRes private val textSizeResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val expectedTextSize = view?.context?.resources?.getDimension(textSizeResource)
        val foundTextSize = (view as? TextView)?.textSize
        if (foundTextSize != expectedTextSize) {
            throw IllegalStateException("Invalid text size! " +
                    "Expected $expectedTextSize, but found $foundTextSize")
        }
    }

}