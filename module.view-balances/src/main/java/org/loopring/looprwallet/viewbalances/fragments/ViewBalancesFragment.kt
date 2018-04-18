package org.loopring.looprwallet.viewbalances.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_view_balances.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthTokenBalanceViewModel
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.adapters.OnTokenLockClickListener
import org.loopring.looprwallet.viewbalances.adapters.ViewBalancesAdapter

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 *
 */
class ViewBalancesFragment : BaseFragment(), OnTokenLockClickListener {

    companion object {
        val TAG: String = ViewBalancesFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_view_balances

    private val tokenBalanceViewModel: EthTokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthTokenBalanceViewModel>(this@ViewBalancesFragment)
    }

    private val adapter by lazy {
        ViewBalancesAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = walletClient.getCurrentWallet()?.credentials?.address
        if (address != null) {
            tokenBalanceViewModel.getAllTokensWithBalances(this, address) {
                adapter.updateData(it)

                // The adapter tracks changes automatically, so we only needed to observe once
                tokenBalanceViewModel.removeDataObserver(this)
            }
        }

        viewBalancesRecycler.layoutManager = LinearLayoutManager(view.context)
        viewBalancesRecycler.adapter = adapter
    }

    override fun onLockClick(token: CryptoToken) {
        TODO("not implemented") // TODO
    }
}