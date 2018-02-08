package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.view.View
import android.widget.TextView

/**
 * Created by Corey on 2/1/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To easily make [Espresso] assertions about a view's top padding
 *
 */
class TopPaddingAssertion(@DimenRes private val dimensionResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val expectedPadding = view?.context?.resources?.getDimension(dimensionResource)?.toInt()
        if (view?.paddingTop != expectedPadding) {
            throw IllegalStateException("Invalid padding! " +
                    "Expected $expectedPadding, but found ${view?.paddingTop}")
        }
    }

}