package com.caplaninnovations.looprwallet.utilities

import android.support.transition.Visibility

/**
 * Created by Corey on 3/9/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 *
 */
fun Visibility.addMode(mode: Int): Visibility {
    this.mode = mode
    return this
}
