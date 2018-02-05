package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
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
class HeightAssertion(@DimenRes private val heightResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val expectedHeight = view?.context?.resources?.getDimension(heightResource)?.toInt()
        if (view?.layoutParams?.height != expectedHeight) {
            throw IllegalStateException("Invalid height! " +
                    "Expected $expectedHeight, but found ${view?.height}")
        }
    }

}