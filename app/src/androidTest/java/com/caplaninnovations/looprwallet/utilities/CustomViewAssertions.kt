package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.view.View
import android.widget.TextView

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class CustomViewAssertions {

    companion object {

        fun alphaIs(expectedAlpha: Float): ViewAssertion {
            return ViewAssertion { view, _ ->
                if (view?.alpha != expectedAlpha) {
                    val actualAlpha = view?.alpha
                    throw IllegalStateException("Invalid alpha! " +
                            "Expected $expectedAlpha, but found $actualAlpha")
                }
            }
        }

        fun heightIs(@DimenRes expectedHeightResource: Int): ViewAssertion {
            return ViewAssertion { view, _ ->
                val expectedHeight = view?.context?.resources?.getDimension(expectedHeightResource)?.toInt()
                if (view?.layoutParams?.height != expectedHeight) {
                    throw IllegalStateException("Invalid height! " +
                            "Expected $expectedHeight, but found ${view?.height}")
                }
            }
        }

        fun textSizeIs(@DimenRes expectedTextSizeResource: Int): ViewAssertion {
            return ViewAssertion { view, _ ->
                val expectedTextSize = view?.context?.resources?.getDimension(expectedTextSizeResource)
                val foundTextSize = (view as? TextView)?.textSize
                if (foundTextSize != expectedTextSize) {
                    throw IllegalStateException("Invalid text size! " +
                            "Expected $expectedTextSize, but found $foundTextSize")
                }
            }
        }

        fun isDisabled(): ViewAssertion {
            return ViewAssertion { view, _ ->
                if (view != null && !view.isEnabled) {
                    throw IllegalStateException("Invalid state! Expected view to be disabled")
                }
            }
        }

        fun topPaddingIs(@DimenRes expectedTopPaddingResource: Int): ViewAssertion {
            return ViewAssertion { view, _ ->
                val expectedPadding = view?.context?.resources?.getDimension(expectedTopPaddingResource)?.toInt()
                if (view?.paddingTop != expectedPadding) {
                    throw IllegalStateException("Invalid padding! " +
                            "Expected $expectedPadding, but found ${view?.paddingTop}")
                }
            }
        }

    }

}