package com.caplaninnovations.looprwallet.adapters.orders

import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.wallet.LooprOrder
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
class OrdersAdapter : RealmRecyclerViewAdapter<LooprOrder, OrdersViewHolder> {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(data: OrderedRealmCollection<LooprOrder>?) : super(data, true)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): OrdersViewHolder {
        val view = View.inflate(parent?.context, R.layout.view_holder_generic_order, parent)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.onBind(data!![position], cancelListener = {
            // TODO
        })
    }


}