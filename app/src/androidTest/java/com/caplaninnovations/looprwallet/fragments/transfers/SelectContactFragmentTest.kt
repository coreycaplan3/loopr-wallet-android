package com.caplaninnovations.looprwallet.fragments.transfers

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import org.junit.Before
import org.junit.Test

/**
 * Created by Corey on 3/30/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class SelectContactFragmentTest : BaseDaggerFragmentTest<SelectContactFragment>() {

    override val fragment = SelectContactFragment()

    override val tag = SelectContactFragment.TAG

    val addressOne = "0x0123456701234567012345670123456701234567"
    val addressTwo = "0x0123456701234567012345670123456701234568"
    val addressThree = "0xabcdefabcdefabcdefabcdefabcdefabcdefabcd"
    val addressFour = "0xabcdefabcdefabcdefabcdefabcdefabcdefabce"

    val nameOne = "Daniel T"
    val nameTwo = "Paige P"
    val nameThree = "Corey C"
    val nameFour = "Adam K"

    @Before
    fun createRealmData() {
        val list = listOf(
                Contact(addressOne, nameOne),
                Contact(addressTwo, nameTwo),
                Contact(addressThree, nameThree),
                Contact(addressFour, nameFour)
        )

        BaseRealmRepository(wallet!!).addList(list)
    }

    @Test
    fun enterInvalidAddress_submitIsDisabled() {

    }

    @Test
    fun enterPartialAddress_checkForFilter() {

    }

    @Test
    fun enterFullAddress_unknown() {

    }

    @Test
    fun enterFullAddress_fromBarcode() {

    }

    @Test
    fun enterFullAddress_isKnown() {

    }

    @Test
    fun enterName_selectName_checkIsValid() {

    }

}