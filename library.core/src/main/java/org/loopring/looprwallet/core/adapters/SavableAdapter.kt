package org.loopring.looprwallet.core.adapters

import android.os.Bundle

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
interface SavableAdapter {

    fun onRestoreInstanceState(savedInstanceState: Bundle?)

    fun onSaveInstanceState(outState: Bundle)

}