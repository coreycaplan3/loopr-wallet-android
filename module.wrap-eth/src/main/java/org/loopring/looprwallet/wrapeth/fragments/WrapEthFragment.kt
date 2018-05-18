package org.loopring.looprwallet.wrapeth.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.View
import kotlinx.android.synthetic.main.fragment_wrap_eth.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.presenters.NumberPadPresenter
import org.loopring.looprwallet.core.presenters.NumberPadPresenter.NumberPadActionListener
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenBalanceViewModel
import org.loopring.looprwallet.wrapeth.R
import org.loopring.looprwallet.wrapeth.dagger.wrapEthLooprComponent
import org.loopring.looprwallet.wrapeth.viewmodels.WrapEthViewModel
import javax.inject.Inject

/**
 * Created by corey on 5/7/18
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class WrapEthFragment : BaseFragment(), NumberPadActionListener {

    companion object {

        val TAG: String = WrapEthFragment::class.java.simpleName

        private const val KEY_IS_SWAPPING_WETH = "IS_SWAPPING_WETH"
    }

    override val layoutResource: Int
        get() = R.layout.fragment_wrap_eth

    @VisibleForTesting
    var isSwappingToWrapped: Boolean = true

    private val tokenBalanceViewModel: EthereumTokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenBalanceViewModel>(this)
    }

    private val depositEthViewModel: WrapEthViewModel by lazy {
        LooprViewModelFactory.get<WrapEthViewModel>(this, "$TAG:deposit")
    }

    private val withdrawEthViewModel: WrapEthViewModel by lazy {
        LooprViewModelFactory.get<WrapEthViewModel>(this, "$TAG:withdraw")
    }

    private val address
        get() = walletClient.getCurrentWallet()?.credentials?.address

    @VisibleForTesting
    var ethToken: LooprToken? = null
        set(value) = synchronized(this) {
            field = value

            val token = field ?: return
            val address = address ?: return
            if (isSwappingToWrapped) {
                bindTokenBalanceToAvailableLabel(address, token)
            }
        }

    @VisibleForTesting
    var wethToken: LooprToken? = null
        set(value) = synchronized(this) {
            field = value

            val token = field ?: return
            val address = address ?: return
            if (!isSwappingToWrapped) {
                bindTokenBalanceToAvailableLabel(address, token)
            }
        }

    override val isDecimalVisible = true

    @Inject
    lateinit var currencySettings: CurrencySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wrapEthLooprComponent.inject(this)
        isSwappingToWrapped = savedInstanceState?.getBoolean(KEY_IS_SWAPPING_WETH, true) ?: true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.title = str(R.string.convert)

        NumberPadPresenter.setupNumberPad(this, this)
        wrapEtherMaxButton.setOnClickListener { onMaxButtonClick() }
        wrapEtherSwapButton.setOnClickListener { onSwapWrappedAndEtherClick() }
        wrapEtherConvertButton.setOnClickListener(::onConvertButtonClick)

        depositEthViewModel.let {
            setupTransactionViewModel(it, R.string.wrapping_eth_into_weth) { _ ->
                TODO("Add me...")
            }
        }

        withdrawEthViewModel.let {
            setupTransactionViewModel(it, R.string.unwrapping_weth_into_eth) { _ ->
                TODO("Add me...")
            }
        }

        wrapEtherInputEditText.inputType = InputType.TYPE_NULL
        wrapEtherInputEditText.setTextIsSelectable(true)

        address?.let { address ->
            setupOfflineFirstStateAndErrorObserver(tokenBalanceViewModel, fragmentContainer)
            tokenBalanceViewModel.getAllTokensWithBalances(this, address) { tokens ->
                ethToken = tokens.where().equalTo(LooprToken::identifier, LooprToken.ETH.identifier).findFirst()
                wethToken = tokens.where().equalTo(LooprToken::identifier, LooprToken.WETH.identifier).findFirst()
            }
        }
    }

    override fun onNumberClick(number: String) {
        val text = wrapEtherInputEditText.text.toString()

        val decimalPoint = currencySettings.getDecimalSeparator().first()
        val decimalIndex = text.indexOfFirst { it == decimalPoint }

        if (decimalIndex > -1 && text.getAmountAfterDecimal() < CurrencyExchangeRate.MAX_EXCHANGE_RATE_FRACTION_DIGITS) {
            // We can append after a decimal if we haven't surpassed the number of digits usable
            appendCharacterToEditText(number)
        }

        if (decimalIndex == -1 && text.getAmountBeforeDecimal() < CurrencyExchangeRate.MAX_INTEGER_DIGITS) {
            // We can append before a decimal if we haven't surpassed the number of digits usable
            appendCharacterToEditText(number)
        }

        onFormChanged()
    }

    override fun onDecimalClick() {
        val text = wrapEtherInputEditText.text.toString()
        val decimalPoint = currencySettings.getDecimalSeparator()
        if (!text.contains(decimalPoint)) {
            appendCharacterToEditText(decimalPoint)
        }

        onFormChanged()
    }

    override fun onBackspaceClick() {
        val text = wrapEtherInputEditText.text.toString()
        if (!text.isEmpty()) {

            val modifiedText = StringBuilder(text)
                    .deleteCharAt(text.length - 1)
                    .toString()

            wrapEtherInputEditText.setText(modifiedText)
            onFormChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IS_SWAPPING_WETH, isSwappingToWrapped)
    }

    // MARK - Private Methods

    override fun onFormChanged() {
        val text = wrapEtherInputEditText.text.toString()
        val number = text.toBigDecimalOrNull()
        wrapEtherConvertButton.isEnabled = !text.isEmpty() && number != null
    }

    private fun onMaxButtonClick() {
        val token = when {
            isSwappingToWrapped -> ethToken ?: return
            else -> wethToken ?: return
        }

        address?.ifNotNull { address ->
            bindTokenBalanceToAvailableLabel(address, token)

            val balance = token.findAddressBalance(address)?.balance?.formatAsToken(currencySettings, token)
            balance?.let { wrapEtherInputEditText.setText(it) }
        }
    }

    private fun onSwapWrappedAndEtherClick() {
        fun changeConstraintLayoutParams(params: ConstraintLayout.LayoutParams?) = when {
            isSwappingToWrapped -> {
                params?.leftToRight = R.id.wrapEthEtherContainer
                params?.rightToLeft = R.id.wrapEtherWethContainer
            }
            else -> {
                params?.leftToRight = R.id.wrapEtherWethContainer
                params?.rightToLeft = R.id.wrapEthEtherContainer
            }
        }

        isSwappingToWrapped = !isSwappingToWrapped

        wrapEtherInputEditText.setText("")

        if (isSwappingToWrapped) {
            wrapEtherTypeLabel.setText(R.string.eth)
        } else {
            wrapEtherTypeLabel.setText(R.string.weth)
        }

        val address = address ?: return
        val token = when {
            isSwappingToWrapped -> ethToken ?: return
            else -> wethToken ?: return
        }
        bindTokenBalanceToAvailableLabel(address, token)

        val temp = wrapEthEtherContainer.layoutParams
        wrapEthEtherContainer.layoutParams = wrapEtherWethContainer.layoutParams
        wrapEtherWethContainer.layoutParams = temp

        changeConstraintLayoutParams(ethToWethTitleLabel.layoutParams as? ConstraintLayout.LayoutParams)
        changeConstraintLayoutParams(wrapEtherSwapButton.layoutParams as? ConstraintLayout.LayoutParams)

        view?.requestLayout()
    }

    private fun onConvertButtonClick(view: View) {
        val ethToken = ethToken ?: return
        val wethToken = wethToken ?: return

        val primaryTicker: String
        val secondaryTicker: String
        when {
            isSwappingToWrapped -> {
                primaryTicker = ethToken.ticker
                secondaryTicker = wethToken.ticker
            }
            else -> {
                primaryTicker = wethToken.ticker
                secondaryTicker = ethToken.ticker
            }
        }

        AlertDialog.Builder(view.context)
                .setTitle(str(R.string.formatter_convert_to).format(primaryTicker))
                .setMessage(str(R.string.formatter_convert_weth_to_eth).format(primaryTicker, secondaryTicker))
                .setPositiveButton(R.string.convert) setPositiveButton@{ dialog, _ ->
                    dialog.dismiss()

                    val amount = wrapEtherInputEditText.text.toString()
                            .toBigDecimalOrNull()
                            ?.toBigInteger(ethToken) ?: return@setPositiveButton

                    (activity as? BaseActivity)?.progressDialog?.let {
                        it.setMessage(getString(R.string.converting))
                        it.show()
                    }

                    val wallet = walletClient.getCurrentWallet() ?: return@setPositiveButton
                    when {
                        isSwappingToWrapped -> depositEthViewModel.convertToWrapped(amount, wallet)
                        else -> withdrawEthViewModel.convertToEther(amount, wallet)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    @SuppressLint("SetTextI18n")
    private fun appendCharacterToEditText(s: String) {
        val cursorPosition = wrapEtherInputEditText.selectionStart
        if (cursorPosition == -1) {
            loge("Cursor position was -1…", IllegalStateException())
            return
        }

        val text = wrapEtherInputEditText.text.toString()
        wrapEtherInputEditText.setText("$text$s")
    }

    private fun bindTokenBalanceToAvailableLabel(address: String, token: LooprToken) {
        val balance = token.findAddressBalance(address)?.balance?.formatAsToken(currencySettings, token)
        if (balance != null) {
            wrapEtherQuantityAvailableLabel.text = str(R.string.formatter_available).format(balance)
        }
    }

}