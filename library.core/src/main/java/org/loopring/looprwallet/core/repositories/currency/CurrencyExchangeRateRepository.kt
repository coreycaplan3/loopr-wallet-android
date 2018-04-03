package org.loopring.looprwallet.core.repositories.currency

import android.arch.lifecycle.LiveData
import org.loopring.looprwallet.core.models.currency.CurrencyExchangeRate
import org.loopring.looprwallet.core.models.wallet.LooprWallet
import org.loopring.looprwallet.core.repositories.BaseRealmRepository

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