package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.view_holder_general_order.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.cryptotokens.EthToken
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.realm.RealmClient
import org.loopring.looprwallet.homeorders.R
import org.loopring.looprwallet.homeorders.dagger.homeOrdersLooprComponent
import javax.inject.Inject

/**
 * Created by Corey Caplan on 4/6/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display orders that appear on the home screen to the user
 *
 * @param isOpen True if this adapter will be showing open orders or false if it'll be past ones.
 */
class GeneralOrderAdapter(private val isOpen: Boolean) : BaseRealmAdapter<EthToken>() {

    companion object {
        const val TYPE_FILTER = 3
    }

    @Inject
    lateinit var realmClient: RealmClient

    init {
        homeOrdersLooprComponent.inject(this)
        runBlocking {
            delay(1000)

            val data = realmClient.getSharedInstance().where<EthToken>().findAll()
            updateData(data)
        }
    }

    override val totalItems: Int?
        get() = null

    override fun getItemViewType(position: Int): Int {
        val type = super.getItemViewType(position)

        return when (type) {
            TYPE_DATA -> when (position) {
                0 -> TYPE_FILTER
                else -> TYPE_DATA
            }
            else -> type
        }
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyGeneralOrderViewHolder(isOpen, parent)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return GeneralOrderViewHolder(parent.inflate(R.layout.view_holder_general_order))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: EthToken) {
        (holder as? EmptyGeneralOrderViewHolder)?.bind()

        // TODO
        (holder as? GeneralOrderViewHolder)?.bind("") {
            holder.orderProgress.progress += 10
            if (holder.orderProgress.progress == 100) {
                holder.orderProgress.progress = 0
            }
        }
    }

}