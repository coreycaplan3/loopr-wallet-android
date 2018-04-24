package org.loopring.looprwallet.transferdetails.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import org.loopring.looprwallet.core.models.transfers.LooprTransfer
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.loopr.LooprTransferRepository

/**
 * Created by Corey on 4/21/2018
 *
 * Project: loopr-android
 *
 * Purpose of Class:
 *
 *
 */
class TransferDetailsViewModel(currentWallet: LooprWallet) : ViewModel() {

    private val repository = LooprTransferRepository(currentWallet)

    private var hash: String? = null
    private var data: LiveData<LooprTransfer>? = null

    fun getTransferByHash(owner: LifecycleOwner, hash: String, onChange: (LooprTransfer) -> Unit) {
        if (data == null || this.hash != hash) {
            // Data isn't initialized or we're going to watch for a different hash
            data = repository.getTransferByHash(hash)
        }

        data?.observe(owner, Observer { transfer -> transfer?.let { onChange(it) } })
    }

}