package org.loopring.looprwallet.viewbalances.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_token_balance.*
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.models.cryptotokens.LooprToken
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ImageUtility
import org.loopring.looprwallet.viewbalances.R

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
class ViewBalancesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    inline fun bind(address: String, token: CryptoToken, crossinline onUnlockClick: () -> Unit) {
        val context = itemView.context
        tokenBalanceImage.setImageDrawable(ImageUtility.getImageFromTicker(token.ticker, context))

        tokenBalanceNameLabel.text = token.name
        tokenBalanceTickerLabel.text = token.ticker

        val balance = token.findAddressBalance(address)
        if (balance != null) {
            tokenBalanceLabel.text = str(R.string.formatter_bal).format(balance)
        }

        when (token.identifier) {
            LooprToken.ETH.identifier -> tokenBalanceUnlockButton.visibility = View.GONE
            else -> {
                tokenBalanceUnlockButton.visibility = View.VISIBLE
                tokenBalanceUnlockButton.setOnClickListener { onUnlockClick() }
            }
        }

        TODO("Check if token is unlocked and display lock icon. If locked, display unlock icon")
    }

}