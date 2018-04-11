package org.loopring.looprwallet.core.repositories.currency

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.kotlin.where
import org.loopring.looprwallet.core.extensions.asLiveData
import org.loopring.looprwallet.core.extensions.equalTo
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
class CurrencyExchangeRateRepository : BaseRealmRepository() {

    override fun getRealm() = realmClient.getSharedInstance()

    fun getCurrencyExchangeRate(currency: String): LiveData<CurrencyExchangeRate> {
        return uiRealm.where<CurrencyExchangeRate>()
                .equalTo(CurrencyExchangeRate::currency, currency)
                .findFirstAsync()
                .asLiveData()
    }

}