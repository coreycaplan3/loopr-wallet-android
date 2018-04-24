package org.loopring.looprwallet.viewbalances.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_view_balances.*
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.cryptotokens.CryptoToken
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthTokenBalanceViewModel
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTransactionViewModel
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.adapters.OnTokenLockClickListener
import org.loopring.looprwallet.viewbalances.adapters.ViewBalancesAdapter
import java.io.IOException

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

    private val tokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthTokenBalanceViewModel>(this)
    }

    private val approveTransactionViewModel by lazy {
        LooprViewModelFactory.get<EthereumTransactionViewModel>(this, "$TAG:approve")
    }

    private val disapproveTransactionViewModel by lazy {
        LooprViewModelFactory.get<EthereumTransactionViewModel>(this, "$TAG:disapprove")
    }

    private val adapter by lazy {
        ViewBalancesAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionViewModel(approveTransactionViewModel, R.string.unlocking_token) {
            return@setupTransactionViewModel when (it) {
            // TODO CHECK GAS
                is IOException -> str(R.string.error_no_connection)
                else -> str(R.string.error_unlocking_token)
            }
        }

        setupTransactionViewModel(disapproveTransactionViewModel, R.string.locking_token) {
            return@setupTransactionViewModel when (it) {
            // TODO CHECK GAS
                is IOException -> str(R.string.error_no_connection)
                else -> str(R.string.error_unlocking_token)
            }

        }

        viewBalancesSwipeRefreshLayout.setOnRefreshListener { refreshAll() }
        setupOfflineFirstStateAndErrorObserver(tokenBalanceViewModel, viewBalancesSwipeRefreshLayout)

        val address = walletClient.getCurrentWallet()?.credentials?.address
        if (address != null) {
            tokenBalanceViewModel.getAllTokensWithBalances(this, address) {
                setupOfflineFirstDataObserverForAdapter(tokenBalanceViewModel, adapter, it)
            }
        }

        viewBalancesRecycler.layoutManager = LinearLayoutManager(view.context)
        viewBalancesRecycler.adapter = adapter
    }

    override fun onTokenLockClick(token: CryptoToken) {
        TODO("Unlock or lock the token, depending on its current allowance being above a certain threshold")
    }

    // MARK - Private Methods

    private fun refreshAll() {
        tokenBalanceViewModel.refresh()
    }

}