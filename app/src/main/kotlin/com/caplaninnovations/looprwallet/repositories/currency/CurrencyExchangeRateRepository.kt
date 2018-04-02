package com.caplaninnovations.looprwallet.repositories.currency

import android.arch.lifecycle.LiveData
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
import com.caplaninnovations.looprwallet.models.currency.CurrencyExchangeRate
import com.caplaninnovations.looprwallet.models.wallet.LooprWallet
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import io.realm.kotlin.where

/**
 * Created by Corey Caplan on 3/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CurrencyExchangeRateRepository(currentWallet: LooprWallet)
    : BaseRealmRepository(currentWallet) {

    fun getCurrencyExchangeRate(currency: String): LiveData<CurrencyExchangeRate> {
        return uiSharedRealm.where<CurrencyExchangeRate>()
                .equalTo(CurrencyExchangeRate::currency, currency)
                .findFirstAsync()
                .asLiveData()
    }

}