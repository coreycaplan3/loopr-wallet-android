package com.caplaninnovations.looprwallet.utilities

import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.*
import android.support.test.espresso.action.GeneralLocation.CENTER
import android.view.View

/**
 * Created by Corey on 3/7/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: For more information on the Android screen coordinates, go to
 * [this](https://stackoverflow.com/questions/11483345/how-do-android-screen-coordinates-work)
 * stack overflow post.
 *
 */
object CustomViewActions {

    /**
     * @param itemView The view holder's item view
     * @param by The number of positions that the view holder should be dragged
     */
    fun dragUpViewHolder(itemView: View, by: Int): ViewAction {
        val endCoordinates = CoordinatesProvider {
            val xy = IntArray(2)
            it.getLocationOnScreen(xy)

            val floatArray = FloatArray(2)
            floatArray[0] = xy[0].toFloat()
            floatArray[1] = xy[1] - (itemView.measuredHeight * 2F * by)
            floatArray
        }

        return ViewActions.actionWithAssertions(
                GeneralSwipeAction(Swipe.FAST, CENTER, endCoordinates, Press.FINGER)
        )
    }

    /**
     * @param itemView The view holder's item view
     * @param by The number of positions that the view holder should be dragged
     */
    fun dragDownViewHolder(itemView: View, by: Int): ViewAction {
        val endCoordinates = CoordinatesProvider {
            val xy = IntArray(2)
            it.getLocationOnScreen(xy)

            val floatArray = FloatArray(2)
            floatArray[0] = xy[0].toFloat()
            floatArray[1] = xy[1] + (itemView.height * 2F * by)
            floatArray
        }

        return ViewActions.actionWithAssertions(
                GeneralSwipeAction(Swipe.FAST, CENTER, endCoordinates, Press.FINGER)
        )
    }

}