package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.DimenRes
import android.support.test.espresso.ViewAssertion
import android.widget.TextView

/**
 * Created by Corey on 2/20/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
object CustomViewAssertions {

    fun alphaIs(expectedAlpha: Float): ViewAssertion {
        return ViewAssertion { view, _ ->
            if (view?.alpha != expectedAlpha) {
                val actualAlpha = view?.alpha
                throw AssertionError(IllegalStateException("Invalid alpha! " +
                        "Expected $expectedAlpha, but found $actualAlpha")
                )
            }
        }
    }

    fun heightIs(@DimenRes expectedHeightResource: Int): ViewAssertion {
        return ViewAssertion { view, _ ->
            val expectedHeight = view?.context?.resources?.getDimension(expectedHeightResource)?.toInt()
            if (view?.layoutParams?.height != expectedHeight) {
                throw AssertionError(IllegalStateException("Invalid height! " +
                        "Expected $expectedHeight, but found ${view?.height}")
                )
            }
        }
    }

    fun scaleXIs(expectedScaleX: Float): ViewAssertion {
        return ViewAssertion { view, _ ->
            val foundScaleX = view?.scaleX
            if (foundScaleX != expectedScaleX) {
                throw AssertionError(IllegalStateException("Invalid scaleX! " +
                        "Expected $expectedScaleX, but found $foundScaleX")
                )
            }
        }
    }

    fun scaleYIs(expectedScaleY: Float): ViewAssertion {
        return ViewAssertion { view, _ ->
            val foundScaleY = view?.scaleY
            if (foundScaleY != expectedScaleY) {
                throw AssertionError(IllegalStateException("Invalid scaleY! " +
                        "Expected $expectedScaleY, but found $foundScaleY")
                )
            }
        }
    }

    fun textSizeIs(@DimenRes expectedTextSizeResource: Int): ViewAssertion {
        return ViewAssertion { view, _ ->
            val expectedTextSize = view?.context?.resources?.getDimension(expectedTextSizeResource)
            val foundTextSize = (view as? TextView)?.textSize
            if (foundTextSize != expectedTextSize) {
                throw AssertionError(IllegalStateException("Invalid text size! " +
                        "Expected $expectedTextSize, but found $foundTextSize")
                )
            }
        }
    }

    fun isDisabled(): ViewAssertion {
        return ViewAssertion { view, _ ->
            if (view == null) {
                throw AssertionError(IllegalStateException("Invalid state! View is null!"))
            } else if (view.isEnabled) {
                throw AssertionError(IllegalStateException("Invalid state! Expected view to be disabled"))
            }
        }
    }

    fun isEnabled(): ViewAssertion {
        return ViewAssertion { view, _ ->
            if (view == null) {
                throw AssertionError(IllegalStateException("Invalid state! View is null!"))
            } else if (!view.isEnabled) {
                throw AssertionError(IllegalStateException("Invalid state! Expected view to be enabled"))
            }
        }
    }

    fun topPaddingIs(@DimenRes expectedTopPaddingResource: Int): ViewAssertion {
        return ViewAssertion { view, _ ->
            val expectedPadding = view?.context?.resources?.getDimension(expectedTopPaddingResource)?.toInt()
            if (view?.paddingTop != expectedPadding) {
                throw AssertionError(IllegalStateException("Invalid padding! " +
                        "Expected $expectedPadding, but found ${view?.paddingTop}")
                )
            }
        }
    }

}