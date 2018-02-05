package com.caplaninnovations.looprwallet.utilities

import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.view.View

/**
 *  Created by Corey on 2/5/2018
 *
 *  Project: loopr-wallet-android
 *
 *  Purpose of Class:
 *
 *
 */
class AlphaAssertion(private val alpha: Float) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view?.alpha != alpha) {
            throw IllegalStateException("Invalid alpha! " +
                    "Expected $alpha, but found ${view?.alpha}")
        }
    }

}