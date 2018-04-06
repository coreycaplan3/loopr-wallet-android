package org.loopring.looprwallet.core.utilities

import android.support.design.R
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/**
 * Created by Corey Caplan on 3/30/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object CustomViewMatchers {

    /**
     * @param text The exact text that's being displayed in the snackbar's message. Used as a
     * predicate to find it.
     */
    fun getSnackbarTextMatcher(text: String): Matcher<View> {
        return allOf(withId(R.id.snackbar_text), withText(text))
    }

    /**
     * @param text The exact text that's being displayed in the snackbar's action. Used as a
     * predicate to find it.
     */
    fun getSnackbarActionMatcher(text: String): Matcher<View> {
        return allOf(withId(R.id.snackbar_action), withText(text))
    }

}