package com.caplaninnovations.looprwallet.handlers

import android.view.View
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import kotlinx.android.synthetic.main.number_pad.*

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
            baseFragment.numberPadDecimal.text = LooprWalletApp.dagger.currencySettings.getDecimalSeparator()
            baseFragment.numberPadDecimal.visibility = View.VISIBLE
            baseFragment.numberPadDecimal.setOnClickListener { listener.onDecimalClick() }
        } else {
            baseFragment.numberPadDecimal.visibility = View.GONE
        }

        baseFragment.numberPadBackspace.setOnClickListener { listener.onBackspaceClick() }
    }

}