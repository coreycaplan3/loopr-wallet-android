package org.loopring.looprwallet.homemarkets.adapters

import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.models.trading.TradingPair
import org.loopring.looprwallet.home.R
import org.loopring.looprwallet.tokendetails.TradingPairDetailsActivity

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsAdapter(collection: OrderedRealmCollection<TradingPair>) :
        RealmRecyclerViewAdapter<TradingPair, MarketsViewHolder>(collection, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketsViewHolder {
        return MarketsViewHolder(parent.inflate(R.layout.view_holder_trading_pair_general))
    }

    override fun onBindViewHolder(holder: MarketsViewHolder, position: Int) {
        holder.onBind(data!![position], clickListener = {
            val context = holder.itemView.context
            val intent = TradingPairDetailsActivity.route(it, context)
            context.startActivity(intent)
        })
    }

}