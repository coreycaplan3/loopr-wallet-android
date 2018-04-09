package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.realm.RealmModel
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter

/**
 * Created by Corey Caplan on 4/6/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 * @param isOpen True if this adapter will be showing open orders or false if it'll be past ones.
 */
class GeneralOrderAdapter(private val isOpen: Boolean) : BaseRealmAdapter<RealmModel>() {

    override val totalItems: Int?
        get() = null

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyGeneralOrderViewHolder(isOpen, parent)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: RealmModel) {
        (holder as? EmptyGeneralOrderViewHolder)?.bind()

        // TODO
        (holder as? GeneralOrderViewHolder)
    }

}