package com.caplaninnovations.looprwallet.adapters

import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.models.wallet.LooprOrder
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class OrderAdapter : RealmRecyclerViewAdapter<LooprOrder, OrderViewHolder> {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(data: OrderedRealmCollection<LooprOrder>?) : super(data, true)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): OrderViewHolder {
        val view = View.inflate(parent?.context, R.layout.order_view_holder, parent)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.onBind(data!![position], cancelListener = {
            // TODO
        })
    }


}