package org.loopring.looprwallet.tradedetails.adapters

import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.loopring.looprwallet.core.models.loopr.orders.AppLooprOrder

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To show orders for a specific trading pair, on the trade-details paige
 *
 */
class TradingPairOrdersAdapter(data: OrderedRealmCollection<AppLooprOrder>?)
    : RealmRecyclerViewAdapter<AppLooprOrder, TradingPairOrdersViewHolder>(data, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradingPairOrdersViewHolder {
        TODO("Create orders for trading pair")
    }

    override fun onBindViewHolder(holderTradingPair: TradingPairOrdersViewHolder, position: Int) {
        holderTradingPair.onBind(data!![position]) {
            // TODO
        }
    }


}