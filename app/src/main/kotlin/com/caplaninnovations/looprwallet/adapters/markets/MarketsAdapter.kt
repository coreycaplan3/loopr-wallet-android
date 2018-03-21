package com.caplaninnovations.looprwallet.adapters.markets

import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.TradingPairDetailsActivity
import com.caplaninnovations.looprwallet.models.trading.TradingPair
import com.caplaninnovations.looprwallet.extensions.inflate
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

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
        return MarketsViewHolder(parent.inflate(R.layout.view_holder_trading_pair))
    }

    override fun onBindViewHolder(holder: MarketsViewHolder, position: Int) {
        holder.onBind(data!![position], clickListener = {
            val context = holder.itemView.context
            val intent = TradingPairDetailsActivity.IntentCreator.createIntent(it, context)
            context.startActivity(intent)
        })
    }

}