package com.caplaninnovations.looprwallet

import org.junit.Before

/**
 * Created by Corey Caplan on 2/3/18.
 * Project: loopr-wallet-android
 * <p></p>
 * Purpose of Class:
 */
abstract class BaseInstrumentedTest {

    @Before
    fun setup() {


        provideDaggerComponent()
    }

    abstract fun provideDaggerComponent() : Any?

}