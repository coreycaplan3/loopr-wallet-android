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
        if (view?.paddingTop != view?.context?.resources?.getDimension(dimensionResource)?.toInt()) {
            throw NoMatchingViewException.Builder().build()
        }
    }

}

class HeightAssertion(@DimenRes private val heightResource: Int): ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view?.layoutParams?.height != view?.context?.resources?.getDimension(heightResource)?.toInt()) {
            throw NoMatchingViewException.Builder().build()
        }
    }

}

class AlphaAssertion(private val alpha: Float) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view?.alpha != alpha) {
            throw NoMatchingViewException.Builder().build()
        }
    }

}

class TextSizeAssertion(@DimenRes private val textSizeResource: Int) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if ((view as? TextView)?.textSize != view?.context?.resources?.getDimension(textSizeResource)) {
            throw NoMatchingViewException.Builder().build()
        }
    }

}