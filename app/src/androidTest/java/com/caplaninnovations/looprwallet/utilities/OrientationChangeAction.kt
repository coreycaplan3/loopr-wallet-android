package com.caplaninnovations.looprwallet.utilities

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - Nathan Barraille
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.runner.lifecycle.Stage
import android.view.View

import org.hamcrest.Matcher

import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.content.ContextWrapper


/**
 * An Espresso ViewAction that changes the orientation of the screen.
 *
 * To use it, invoke it like the following:
 * ~~~~
 * onView(isRoot()).perform(OrientationChangeAction.changeOrientationToLandscape())
 *
 * onView(isRoot()).perform(OrientationChangeAction.changeOrientationToPortrait())
 * ~~~~
 */
class OrientationChangeAction private constructor(private val orientation: Int) : ViewAction {

    companion object {
        fun changeOrientationToLandscape(): ViewAction {
            return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

        fun changeOrientationToPortrait(): ViewAction {
            return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }

    override fun getConstraints(): Matcher<View> = isRoot()

    override fun getDescription(): String = "Changing orientation to $orientation"

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.loopMainThreadUntilIdle()
        var activity: Activity? = view!!.context as? Activity

        if(activity == null) {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    activity = context
                }
                context = context.baseContext
            }
        }

        activity!!.requestedOrientation = orientation

        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)

        if (resumedActivities.isEmpty()) {
            throw RuntimeException("Could not change orientation")
        }
    }

}
