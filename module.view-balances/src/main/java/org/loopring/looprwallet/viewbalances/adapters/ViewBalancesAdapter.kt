package org.loopring.looprwallet.viewbalances.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.extensions.inflate
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.wallet.WalletClient
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.dagger.viewBalancesLooprComponent
import javax.inject.Inject

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
class ViewBalancesAdapter(listener: OnTokenLockClickListener) : BaseRealmAdapter<CryptoToken>() {

    override val totalItems: Int? = null

    @Inject
    lateinit var walletClient: WalletClient

    private val listener by weakReference(listener)

    init {
        viewBalancesLooprComponent.inject(this)
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw IllegalStateException("This method should never be called")
    }

    override fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewBalancesViewHolder(parent.inflate(R.layout.view_holder_token_balance))
    }

    override fun getDataOffset(position: Int): Int? = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: CryptoToken?) {
        item ?: return
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return

        (holder as? ViewBalancesViewHolder)?.bind(address, item) {
            listener?.onTokenLockClick(item)
        }
    }

}