package org.loopring.looprwallet.order.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.realm.RealmModel
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
class OrderDetailsAdapter : BaseRealmAdapter<RealmModel>() {

    override val totalItems: Int?
        get() = TODO("not implemented") // TODO

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw NotImplementedError("This method should never be called, since we already had SOME " +
                "data before reaching this adapter")
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        TODO("not implemented") // TODO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: RealmModel) {
        TODO("not implemented") // TODO
    }

    override fun getItemCount(): Int {
        return 1 + (data?.let { getItemCountForOnlyData(it) } ?: 0)
    }
}