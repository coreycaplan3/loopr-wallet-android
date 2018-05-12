package org.loopring.looprwallet.viewbalances.fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_view_balances.*
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.core.extensions.snackbar
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.settings.EthereumFeeSettings
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.viewmodels.LooprViewModelFactory
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenBalanceViewModel
import org.loopring.looprwallet.core.viewmodels.eth.EthereumTokenTransactionViewModel
import org.loopring.looprwallet.viewbalances.R
import org.loopring.looprwallet.viewbalances.adapters.OnTokenLockClickListener
import org.loopring.looprwallet.viewbalances.adapters.ViewBalancesAdapter
import org.loopring.looprwallet.viewbalances.dagger.viewBalancesLooprComponent
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by Corey on 4/18/2018
 *
 * Project: loopr-android-official
 *
 * Purpose of Class:
 *
 */
class ViewBalancesFragment : BaseFragment(), OnTokenLockClickListener {

    companion object {
        val TAG: String = ViewBalancesFragment::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.fragment_view_balances

    private val tokenBalanceViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenBalanceViewModel>(this)
    }

    // TODO load allowances of the delegate
    private val approveTransactionViewModel: EthereumTokenTransactionViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenTransactionViewModel>(this, "$TAG:approve")
    }

    private val disapproveTransactionViewModel: EthereumTokenTransactionViewModel by lazy {
        LooprViewModelFactory.get<EthereumTokenTransactionViewModel>(this, "$TAG:disapprove")
    }

    private val adapter by lazy {
        ViewBalancesAdapter(this)
    }

    @Inject
    lateinit var ethereumFeeSettings: EthereumFeeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBalancesLooprComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.title = str(R.string.my_balances)

        setupLockAndUnlockTransactionViewModel(approveTransactionViewModel)
        approveTransactionViewModel.result.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            onUnlockSuccess(it.first)
        }

        setupLockAndUnlockTransactionViewModel(disapproveTransactionViewModel)
        disapproveTransactionViewModel.result.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            onLockSuccess(it.first)
        }

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

    override fun onTokenLockClick(token: LooprToken) {
        val context = context ?: return

        val adapter = ArrayAdapter.createFromResource(context, R.array.lock_or_unlock_token, android.R.layout.simple_spinner_dropdown_item)

        AlertDialog.Builder(context)
                .setTitle(getString(R.string.formatter_unlock_or_lock_token).format(token.name))
                .setAdapter(adapter) { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Index 0 --> Unlock; Index 1 --> Lock

                        val wallet = walletClient.getCurrentWallet() ?: return
                        val loopringSmartContractDelegate = LooprToken.LRC.identifier
                        val amount = token.totalSupply
                        val gasPrice = ethereumFeeSettings.gasPriceInGwei
                        val gasLimit = ethereumFeeSettings.wethDepositGasLimit
                        when (position) {
                            0 -> approveTransactionViewModel.approveToken(token, wallet, loopringSmartContractDelegate, amount, gasLimit, gasPrice)
                            1 -> disapproveTransactionViewModel.approveToken(token, wallet, loopringSmartContractDelegate, BigInteger.ZERO, gasLimit, gasPrice)
                        }
                        TODO("You need to to add the transferDelegate information!")
                    }
                })
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

    // MARK - Private Methods

    private fun setupLockAndUnlockTransactionViewModel(viewModel: EthereumTokenTransactionViewModel?) {
        viewModel?.isTransactionRunning?.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            val progress = (activity as? BaseActivity)?.progressDialog
            if (it) {
                progress?.show()
            } else if (progress?.isShowing == true) {
                progress.dismiss()
            }
        }

        viewModel?.error?.observeForDoubleSpend(fragmentViewLifecycleFragment!!) {
            // TODO
        }
    }

    private fun onLockSuccess(token: LooprToken) {
        val message = getString(R.string.formatter_locked_token).format(token.name)
        view?.snackbar(message)
    }

    private fun onUnlockSuccess(token: LooprToken) {
        val message = getString(R.string.formatter_unlocked_token).format(token.name)
        view?.snackbar(message)
    }

}