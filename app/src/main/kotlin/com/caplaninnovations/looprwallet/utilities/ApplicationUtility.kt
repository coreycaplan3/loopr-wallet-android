package com.caplaninnovations.looprwallet.utilities

import android.support.annotation.StringRes
import com.caplaninnovations.looprwallet.application.LooprWalletApp

/**
 * Created by Corey Caplan on 1/19/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * Gets a string using the current application's context instance
 */
fun str(@StringRes resource: Int) = LooprWalletApp.application.getString(resource)