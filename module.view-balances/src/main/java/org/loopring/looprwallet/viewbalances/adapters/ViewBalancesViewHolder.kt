package org.loopring.looprwallet.viewbalances.adapters

import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_token_balance.*
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import org.loopring.looprwallet.core.models.loopr.tokens.CryptoToken
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.col
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ImageUtility
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.dagger.viewBalancesLooprComponent
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 */
class ViewBalancesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    @Inject
    lateinit var currencySettings: CurrencySettings

    override val containerView: View?
        get() = itemView

    init {
        viewBalancesLooprComponent.inject(this)
    }

    inline fun bind(address: String, token: LooprToken, crossinline onUnlockClick: () -> Unit) {
        val context = itemView.context
        tokenBalanceImage.setImageDrawable(ImageUtility.getImageFromTicker(token.ticker, context))

        tokenBalanceNameLabel.text = token.name
        tokenBalanceTickerLabel.text = token.ticker

        val balance = token.findAddressBalance(address)?.balance
        if (balance != null) {
            tokenBalanceLabel.text = str(R.string.formatter_bal).format(balance.formatAsToken(currencySettings, token))
        } else {
            tokenBalanceLabel.text = str(R.string.formatter_bal).format("0")
        }

        when (token.identifier) {
            LooprToken.ETH.identifier -> tokenBalanceUnlockButton.visibility = View.GONE
            else -> {
                tokenBalanceUnlockButton.visibility = View.VISIBLE
                tokenBalanceUnlockButton.setOnClickListener { onUnlockClick() }
            }
        }

        val allowance = token.findAddressAllowance(address)
        val lockIcon = if (allowance == null || allowance == BigInteger.ZERO) {
            ApplicationUtility.drawable(R.drawable.ic_lock_outline_white_24dp, itemView.context)
        } else {
            ApplicationUtility.drawable(R.drawable.ic_lock_open_white_24dp, itemView.context)
        }

        val theme = itemView.context.theme
        DrawableCompat.setTint(lockIcon, col(theme.getResourceIdFromAttrId(R.attr.textColorPrimaryOnly)))

        tokenBalanceUnlockButton.setImageDrawable(lockIcon)
    }

}