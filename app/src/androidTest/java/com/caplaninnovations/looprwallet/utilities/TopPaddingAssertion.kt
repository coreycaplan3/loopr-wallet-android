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

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val expectedPadding = view?.context?.resources?.getDimension(dimensionResource)?.toInt()
        if (view?.paddingTop != expectedPadding) {
            throw IllegalStateException("Invalid padding! " +
                    "Expected $expectedPadding, but found ${view?.paddingTop}")
        }
    }

}