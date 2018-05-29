package org.loopring.looprwallet.viewbalances.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.models.loopr.paging.DefaultLooprPagerAdapter
import org.loopring.looprwallet.core.models.loopr.paging.LooprAdapterPager
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
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
class ViewBalancesAdapter(private val listener: OnTokenLockClickListener) : BaseRealmAdapter<LooprToken>() {

    @Inject
    lateinit var walletClient: WalletClient

    override var pager: LooprAdapterPager<LooprToken> = DefaultLooprPagerAdapter()

    init {
        viewBalancesLooprComponent.inject(this)
    }

    override fun onCreateEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        throw IllegalStateException("This method should never be called")
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = getInflater(parent)
        val view = inflater.inflate(R.layout.view_holder_token_balance, parent, false)
        return ViewBalancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, index: Int, item: LooprToken?) {
        item ?: return
        val address = walletClient.getCurrentWallet()?.credentials?.address ?: return

        (holder as? ViewBalancesViewHolder)?.bind(address, item) {
            listener.onTokenLockClick(item)
        }
    }

}