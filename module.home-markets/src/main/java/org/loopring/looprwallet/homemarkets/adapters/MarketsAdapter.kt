package org.loopring.looprwallet.homemarkets.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.realm.RealmModel
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.guard
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.homemarkets.R
import org.loopring.looprwallet.tradedetails.activities.TradingPairDetailsActivity

/**
 * Created by Corey Caplan on 1/29/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param onRefresh A function that's invoked if the user needs to refresh the data.
 */
class MarketsAdapter(activity: BaseActivity, onRefresh: () -> Unit) : BaseRealmAdapter<RealmModel>() {

    companion object {

        const val TYPE_MARKETS_FILTER = 3
    }

    private val activity by weakReference(activity)
    private val onRefresh by weakReference(onRefresh)

    override val totalItems: Int?
        get() = TODO("not implemented")

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MarketsEmptyViewHolder(parent.inflate(R.layout.view_holder_markets_empty)) {
            onRefresh?.invoke()
        }
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_DATA -> MarketsViewHolder(parent.inflate(R.layout.view_holder_markets))
        TYPE_MARKETS_FILTER -> throw NotImplementedError("")
        else -> throw NotImplementedError("")
    }

    override fun getDataOffset(position: Int) = when (position) {
        0 -> null
        else -> position - 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: RealmModel?) {


        item?.guard { } ?: return
        (holder as? MarketsViewHolder)?.bind(item) { tradingPair ->
            activity?.let { TradingPairDetailsActivity.route(tradingPair, it) }
        }
    }

}