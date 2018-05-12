package org.loopring.looprwallet.hometransfers.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_view_transfers.*
import org.loopring.looprwallet.core.extensions.formatAsCurrency
import org.loopring.looprwallet.core.extensions.formatAsToken
import org.loopring.looprwallet.core.models.settings.CurrencySettings
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.hometransfers.dagger.homeTransfersLooprComponent
import javax.inject.Inject

/**
 * Created by Corey Caplan on 4/20/18.
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 */
class ViewTransfersViewHolder(itemView: View?, onTransferClick: (Int) -> Unit)
    : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @Inject
    lateinit var currencySettings: CurrencySettings

    init {
        homeTransfersLooprComponent.inject(this)

        itemView?.setOnClickListener { onTransferClick(adapterPosition) }
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: LooprTransfer) {
        if (item.isSend) {
            viewTransfersSendImage.visibility = View.VISIBLE
            viewTransfersReceiveImage.visibility = View.GONE
        } else {
            viewTransfersReceiveImage.visibility = View.VISIBLE
            viewTransfersSendImage.visibility = View.GONE
        }

        viewTransfersTokenNameLabel.text = item.token.name
        viewTransfersContactLabel.text = item.contactAddress
        viewTransfersQuantityLabel.text = item.numberOfTokens.formatAsToken(currencySettings, item.token)
        viewTransfersPriceLabel.text = item.usdValue.formatAsCurrency(currencySettings)
    }

}