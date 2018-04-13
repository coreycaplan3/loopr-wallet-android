package org.loopring.looprwallet.homemarkets.adapters

import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.markets.TradingPair
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class MarketsAdapter(collection: OrderedRealmCollection<TradingPair>?, activity: BaseActivity) :
        RealmRecyclerViewAdapter<TradingPair, MarketsViewHolder>(collection, true) {

    private val activity by weakReference(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketsViewHolder {
        return MarketsViewHolder(parent.inflate(R.layout.view_holder_markets))
    }

    override fun onBindViewHolder(holder: MarketsViewHolder, position: Int) {
        holder.onBind(data!![position]) { tradingPair ->
            activity?.let { TradingPairDetailsActivity.route(tradingPair, it) }
        }
    }

}