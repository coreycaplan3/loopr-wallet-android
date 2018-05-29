package org.loopring.looprwallet.createorder.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java.math.BigDecimal

/**
 * Created by corey on 5/29/18
 *
 * Project: loopr-android
 *
 * Purpose of Class: A [ViewModel] for passing a custom price from the EnterPriceFragment to
 * the CreateOrderFragment
 *
 */
class CreateOrderPriceViewModel: ViewModel() {

    val priceLiveData: MutableLiveData<BigDecimal> = MutableLiveData()

}