package org.loopring.looprwallet.core.extensions

import android.os.Build

/**
 * Created by Corey on 1/18/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class: To create utility methods for commonly-done things, to streamline code and
 * make it more readable
 *
 */

/**
 * @return True if running on API version >= 16
 */
fun isJellybean(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

/**
 * @return True if running on API version >= 18
 */
fun isJellybeanR1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

/**
 * @return True if running on API version >= 18
 */
fun isJellybeanR2(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2

/**
 * @return True if running on API version >= 19
 */
fun isKitkat(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

/**
 * @return  True if running on API version >= 21
 */
fun isLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

/**
 * @return True if running on API version >= 23
 */
fun isMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M