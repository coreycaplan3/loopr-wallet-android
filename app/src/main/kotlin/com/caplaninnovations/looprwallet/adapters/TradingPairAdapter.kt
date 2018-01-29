package com.caplaninnovations.looprwallet.adapters

import android.view.View
import android.view.ViewGroup
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.TradingPairActivity
import com.caplaninnovations.looprwallet.models.wallet.TradingPair
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Corey Caplan on 1/29/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
class TradingPairAdapter : RealmRecyclerViewAdapter<TradingPair, TradingPairViewHolder> {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(collection: OrderedRealmCollection<TradingPair>) : super(collection, true)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TradingPairViewHolder {
        val view = View.inflate(parent?.context, R.layout.trading_pair_view_holder, parent)
        return TradingPairViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradingPairViewHolder, position: Int) {
        holder.onBind(data!![position], clickListener = {
            val context = holder.itemView.context
            val intent = TradingPairActivity.IntentCreator.createIntent(it, context)
            context.startActivity(intent)
        })
    }

}