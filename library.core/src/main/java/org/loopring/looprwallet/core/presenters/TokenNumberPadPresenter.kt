package org.loopring.looprwallet.core.presenters

import android.annotation.SuppressLint
import android.widget.EditText
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.getAmountAfterDecimal
import org.loopring.looprwallet.core.extensions.getAmountBeforeDecimal
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import java.math.BigDecimal
import javax.inject.Inject

/**
 * A presenter used for binding custom keyboard events for inputting token numeric values
 */
class TokenNumberPadPresenter(
        fragment: BaseFragment,
        private val editText: EditText,
        private val onFormChanged: () -> Unit
) : NumberPadPresenter.NumberPadActionListener {

    companion object {

        /**
         * @return True if the provided value is a valid quantity for a token or false if it's not
         */
        fun isValidTokenValue(value: String) = value.toBigDecimalOrNull()?.let {
            it > BigDecimal.ZERO
        } ?: false

    }

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        coreLooprComponent.inject(this)

        NumberPadPresenter.setupNumberPad(fragment, this)
    }

    override fun onNumberClick(number: String) {
        val text = editText.text.toString()

        val decimalPoint = currencySettings.getDecimalSeparator().first()
        val decimalIndex = text.indexOfFirst { it == decimalPoint }

        when {
            number == "0" && text == "0" -> return
            text == "0" -> editText.setText(number)
            decimalIndex > -1 && text.getAmountAfterDecimal() < CurrencyExchangeRate.MAX_EXCHANGE_RATE_FRACTION_DIGITS ->
                // We can append after a decimal if we haven't surpassed the number of digits usable
                appendCharacterToEditText(number)
            decimalIndex == -1 && text.getAmountBeforeDecimal() < CurrencyExchangeRate.MAX_INTEGER_DIGITS ->
                // We can append before a decimal if we haven't surpassed the number of digits usable
                appendCharacterToEditText(number)
        }

        onFormChanged()
    }

    override fun onDecimalClick() {
        val text = editText.text.toString()
        val decimalPoint = currencySettings.getDecimalSeparator()
        when {
            text.isEmpty() -> appendCharacterToEditText("0$decimalPoint")
            !text.contains(decimalPoint) -> appendCharacterToEditText(decimalPoint)
        }

        onFormChanged()
    }

    override fun onBackspaceClick() {
        val text = editText.text.toString()
        if (!text.isEmpty()) {

            val modifiedText = StringBuilder(text)
                    .deleteCharAt(text.length - 1)
                    .toString()

            editText.setText(modifiedText)
            onFormChanged()
        }
    }

    override val isDecimalVisible: Boolean = true

    @SuppressLint("SetTextI18n")
    private fun appendCharacterToEditText(s: String) {
        val text = editText.text.toString()
        editText.setText("$text$s")
    }

}