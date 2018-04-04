package org.loopring.looprwallet.core.handlers

import android.view.View
import kotlinx.android.synthetic.main.number_pad.*
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.fragments.BaseFragment

/**
 * Created by Corey Caplan on 3/26/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A class that contains logic for number pad button clicks.
 */
object NumberPadHandler {

    interface NumberPadActionListener {

        /**
         * The click handler that's used for resolving numeric clicks
         *
         * @param number The number that was clicked, can be 0-9 inclusively.
         */
        fun onNumberClick(number: String)

        /**
         * The method that's called when a decimal is clicked
         */
        fun onDecimalClick()

        /**
         * The method that's called when a backspace is clicked
         */
        fun onBackspaceClick()

        /**
         * True if the decimal symbol should be shown or false otherwise
         */
        val isDecimalVisible: Boolean
    }

    /**
     * @param baseFragment The fragment that is holding the layout
     * @param listener The listener used for sending clicks back to the caller
     */
    fun setupNumberPad(baseFragment: BaseFragment, listener: NumberPadActionListener) {
        baseFragment.numberPadZero.setOnClickListener { listener.onNumberClick("0") }
        baseFragment.numberPadOne.setOnClickListener { listener.onNumberClick("1") }
        baseFragment.numberPadTwo.setOnClickListener { listener.onNumberClick("2") }
        baseFragment.numberPadThree.setOnClickListener { listener.onNumberClick("3") }
        baseFragment.numberPadFour.setOnClickListener { listener.onNumberClick("4") }
        baseFragment.numberPadFive.setOnClickListener { listener.onNumberClick("5") }
        baseFragment.numberPadSix.setOnClickListener { listener.onNumberClick("6") }
        baseFragment.numberPadSeven.setOnClickListener { listener.onNumberClick("7") }
        baseFragment.numberPadEight.setOnClickListener { listener.onNumberClick("8") }
        baseFragment.numberPadNine.setOnClickListener { listener.onNumberClick("9") }

        if (listener.isDecimalVisible) {
            baseFragment.numberPadDecimal.text = coreLooprComponent.currencySettings.getDecimalSeparator()
            baseFragment.numberPadDecimal.visibility = View.VISIBLE
            baseFragment.numberPadDecimal.setOnClickListener { listener.onDecimalClick() }
        } else {
            baseFragment.numberPadDecimal.visibility = View.GONE
        }

        baseFragment.numberPadBackspace.setOnClickListener { listener.onBackspaceClick() }
    }

    fun enableNumberPad(baseFragment: BaseFragment) {
        baseFragment.numberPadZero.isEnabled = true
        baseFragment.numberPadOne.isEnabled = true
        baseFragment.numberPadTwo.isEnabled = true
        baseFragment.numberPadThree.isEnabled = true
        baseFragment.numberPadFour.isEnabled = true
        baseFragment.numberPadFive.isEnabled = true
        baseFragment.numberPadSix.isEnabled = true
        baseFragment.numberPadSeven.isEnabled = true
        baseFragment.numberPadEight.isEnabled = true
        baseFragment.numberPadNine.isEnabled = true

        baseFragment.numberPadBackspace.isEnabled = true
    }

    fun isNumberPadEnabled(baseFragment: BaseFragment) = listOf(
            baseFragment.numberPadZero.isEnabled,
            baseFragment.numberPadOne.isEnabled,
            baseFragment.numberPadTwo.isEnabled,
            baseFragment.numberPadThree.isEnabled,
            baseFragment.numberPadFour.isEnabled,
            baseFragment.numberPadFive.isEnabled,
            baseFragment.numberPadSix.isEnabled,
            baseFragment.numberPadSeven.isEnabled,
            baseFragment.numberPadEight.isEnabled,
            baseFragment.numberPadNine.isEnabled,
            baseFragment.numberPadBackspace.isEnabled
    ).all { it }

    fun isNumberPadDisabled(baseFragment: BaseFragment) = listOf(
            baseFragment.numberPadZero.isEnabled,
            baseFragment.numberPadOne.isEnabled,
            baseFragment.numberPadTwo.isEnabled,
            baseFragment.numberPadThree.isEnabled,
            baseFragment.numberPadFour.isEnabled,
            baseFragment.numberPadFive.isEnabled,
            baseFragment.numberPadSix.isEnabled,
            baseFragment.numberPadSeven.isEnabled,
            baseFragment.numberPadEight.isEnabled,
            baseFragment.numberPadNine.isEnabled,
            baseFragment.numberPadBackspace.isEnabled
    ).all { !it }

    fun disableNumberPad(baseFragment: BaseFragment) {
        baseFragment.numberPadZero.isEnabled = false
        baseFragment.numberPadOne.isEnabled = false
        baseFragment.numberPadTwo.isEnabled = false
        baseFragment.numberPadThree.isEnabled = false
        baseFragment.numberPadFour.isEnabled = false
        baseFragment.numberPadFive.isEnabled = false
        baseFragment.numberPadSix.isEnabled = false
        baseFragment.numberPadSeven.isEnabled = false
        baseFragment.numberPadEight.isEnabled = false
        baseFragment.numberPadNine.isEnabled = false

        baseFragment.numberPadBackspace.isEnabled = false
    }

}