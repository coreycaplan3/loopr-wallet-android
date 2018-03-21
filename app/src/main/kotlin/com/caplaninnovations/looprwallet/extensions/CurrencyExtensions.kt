package com.caplaninnovations.looprwallet.extensions

import com.caplaninnovations.looprwallet.models.android.settings.CurrencySettings
import java.math.BigDecimal

/**
 * Created by Corey Caplan on 3/13/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * Formats a [BigDecimal] in the user's native currency
 */
fun BigDecimal.formatAsCurrency(settings: CurrencySettings): String {
    return settings.getCurrencyInstance().format(this)
}

/**
 * Formats a [BigDecimal] in the user's native currency
 */
fun BigDecimal.formatAsToken(settings: CurrencySettings, tokenTicker: String): String {
    val value = settings.getTokenInstance().format(this)
    return "$value $tokenTicker"
}