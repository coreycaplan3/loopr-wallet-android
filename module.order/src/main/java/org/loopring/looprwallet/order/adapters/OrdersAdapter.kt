package org.loopring.looprwallet.order.adapters

import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.models.order.LooprOrder
import org.loopring.looprwallet.order.R

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class OrdersAdapter : RealmRecyclerViewAdapter<LooprOrder, OrdersViewHolder> {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(data: OrderedRealmCollection<LooprOrder>?) : super(data, true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(parent.inflate(R.layout.view_holder_generic_order))
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.onBind(data!![position], cancelListener = {
            // TODO
        })
    }


}