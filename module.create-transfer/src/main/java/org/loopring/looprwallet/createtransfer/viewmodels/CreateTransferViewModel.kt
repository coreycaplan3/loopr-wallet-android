package org.loopring.looprwallet.createtransfer.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.android.UI
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.repositories.eth.EthTokenRepository

/**
 * Created by corey on 5/31/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A [ViewModel] for passing data between fragments and persisting it between
 * rotations in the *CreateTransfer* flow.
 *
 */
class CreateTransferViewModel : ViewModel() {

    val allTokens: LiveData<OrderedRealmCollection<LooprToken>>

    val currentToken: MutableLiveData<LooprToken> = MutableLiveData()

    val ethToken: LiveData<LooprToken> = MutableLiveData()

    private val repository = EthTokenRepository()

    private val allTokensObserver by lazy {
        Observer<OrderedRealmCollection<LooprToken>> {
            if (it == null) {
                return@Observer
            }

            val snapshot = it.createSnapshot()
            val ethTokenIndex = snapshot.binarySearch(LooprToken.ETH, Comparator { o1, o2 ->
                o1.name.compareTo(o2.name)
            })

            if (ethToken.value == null && ethTokenIndex >= 0) {
                (ethToken as MutableLiveData).value = snapshot[ethTokenIndex]
            }

            if (currentToken.value == null && ethTokenIndex >= 0) {
                currentToken.value = snapshot[ethTokenIndex]
            }
        }
    }

    init {
        allTokens = repository.getAllTokens(UI).apply {
            this.observeForever(allTokensObserver)
        }
    }

    override fun onCleared() {
        super.onCleared()

        allTokens.removeObserver(allTokensObserver)
    }

}