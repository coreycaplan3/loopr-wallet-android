package com.caplaninnovations.looprwallet.fragments.transfers

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.v7.widget.RecyclerView
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BarcodeCaptureActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import com.caplaninnovations.looprwallet.adapters.contacts.ContactsAdapter
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.handlers.BarcodeCaptureHandler
import com.caplaninnovations.looprwallet.models.user.Contact
import com.caplaninnovations.looprwallet.repositories.BaseRealmRepository
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.CustomViewAssertions
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
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

    private val addressOne = "0x0123456701234567012345670123456701234567"
    private val addressTwo = "0x0123456701234567012345670123456701234568"
    private val addressThree = "0xabcdefabcdefabcdefabcdefabcdefabcdefabcd"
    private val addressFour = "0xabcdefabcdefabcdefabcdefabcdefabcdefabce"

    private val nameOne = "Adam"
    private val nameTwo = "Corey"
    private val nameThree = "Daniel"
    private val nameFour = "Paige"

    @Before
    fun createRealmData() = runBlockingUiCode {
        val list = listOf(
                Contact(addressOne, nameOne),
                Contact(addressTwo, nameTwo),
                Contact(addressThree, nameThree),
                Contact(addressFour, nameFour)
        )

        BaseRealmRepository(wallet!!).addList(list)
    }

    @Test
    fun enterInvalidAddress_noResults_submitIsDisabled() {
        val invalidAddress = "0bfdhjsafda"

        Espresso.onView(`is`(fragment.recipientAddressEditText))
                .perform(typeText(invalidAddress), closeSoftKeyboard())

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter
        assertEquals(1, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_EMPTY, adapter.getItemViewType(0))

        assertNull(fragment.selectedContactAddress)

        checkAddContactButtonHidden()

        checkSubmitDisabled()
    }

    @Test
    fun enterPartialAddress_checkForFilter_thenSelect() {
        Espresso.onView(`is`(fragment.recipientAddressEditText))
                .perform(typeText(addressOne.substring(0, 8)), closeSoftKeyboard())

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter
        assertEquals(2, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_DATA, adapter.getItemViewType(0))

        assertNull(fragment.selectedContactAddress)

        checkAddContactButtonHidden()

        checkSubmitDisabled()

        Espresso.onView(`is`(fragment.viewContactsRecyclerView))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        assertEquals(1, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_DATA, adapter.getItemViewType(0))

        assertEquals(addressTwo, fragment.selectedContactAddress)

        val expectedRecipientName = str(R.string.formatter_recipient).format(nameTwo)
        assertEquals(expectedRecipientName, fragment.recipientNameLabel.text.toString())

        checkAddContactButtonVisible()

        checkSubmitEnabled()

    }

    @Test
    fun enterFullAddress_unknownInDatabase() {
        val unknownAddress = "0x1234512345123451234512345123451234512345"
        Espresso.onView(`is`(fragment.recipientAddressEditText))
                .perform(typeText(unknownAddress), closeSoftKeyboard())

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter
        assertEquals(1, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_DATA, adapter.getItemViewType(0))

        // The selectedContactAddress is null when the address is unknown
        assertNull(fragment.selectedContactAddress)

        val expectedRecipientName = str(R.string.formatter_recipient).format(str(R.string.unknown))
        assertEquals(expectedRecipientName, fragment.recipientNameLabel.text.toString())

        checkAddContactButtonVisible()

        checkSubmitEnabled()
    }

    @Test
    fun enterFullAddress_fromBarcode() {
        runBlockingUiCode {
            val intent = Intent().putExtra(BarcodeCaptureActivity.KEY_BARCODE_VALUE, addressOne)
            fragment.onActivityResult(BarcodeCaptureHandler.REQUEST_CODE_START_BARCODE_ACTIVITY, RESULT_OK, intent)
        }

        assertEquals(addressOne, fragment.selectedContactAddress)

        val expectedRecipientName = str(R.string.formatter_recipient).format(nameOne)
        assertEquals(expectedRecipientName, fragment.recipientNameLabel.text.toString())

        checkAddContactButtonHidden()

        checkSubmitEnabled()
    }

    @Test
    fun enterFullAddress_isKnown() {
        Espresso.onView(`is`(fragment.recipientAddressEditText))
                .perform(typeText(addressThree), closeSoftKeyboard())

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter
        assertEquals(1, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_DATA, adapter.getItemViewType(0))

        // The selectedContactAddress is null when the address is unknown
        assertNull(fragment.selectedContactAddress)

        assertEquals(addressThree, fragment.selectedContactAddress)

        val expectedRecipientName = str(R.string.formatter_recipient).format(nameThree)
        assertEquals(expectedRecipientName, fragment.recipientNameLabel.text.toString())

        checkAddContactButtonHidden()

        checkSubmitEnabled()
    }

    @Test
    fun enterName_selectName_isValid() {
        Espresso.onView(withId(R.id.menu_search_contacts))
                .perform(click())

        checkToolbarSearchActive()

        Espresso.onView(withId(R.id.search_src_text))
                .perform(typeText(nameOne))

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter
        assertEquals(1, adapter.itemCount)
        assertEquals(BaseRealmAdapter.TYPE_DATA, adapter.getItemViewType(0))

        Espresso.onView(`is`(fragment.viewContactsRecyclerView))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        checkToolbarSearchInactive()

        val expectedName = str(R.string.formatter_recipient).format(nameOne)
        assertEquals(expectedName, fragment.recipientNameLabel.text.toString())

        assertEquals(addressOne, fragment.recipientAddressEditText.text.toString())

        checkAddContactButtonHidden()

        checkSubmitEnabled()
    }

    @Test
    fun enterName_cancelInput_listIsResetAfterCancel() {
        Espresso.onView(`is`(fragment.recipientAddressEditText))
                .perform(typeText(addressThree.substring(0, 8)))

        val adapter = fragment.viewContactsRecyclerView.adapter as ContactsAdapter

        assertEquals(2, adapter.itemCount)

        Espresso.onView(withId(R.id.menu_search_contacts))
                .perform(click())

        checkToolbarSearchActive()

        Espresso.onView(withId(R.id.search_src_text))
                .perform(typeText(nameOne))

        assertEquals(1, adapter)

        // Cancel the search and reset the RecyclerView's content
        Espresso.pressBack()

        checkToolbarSearchInactive()

        // We should go back to having 2 people available
        assertEquals(2, adapter.itemCount)

        assertEquals(addressThree, adapter.data!![0].address)
        assertEquals(addressFour, adapter.data!![1].address)
    }

    // MARK - Private Methods

    private fun checkAddContactButtonVisible() {
        Espresso.onView(`is`(fragment.addContactButton))
                .check(matches(withEffectiveVisibility(VISIBLE)))
    }

    private fun checkAddContactButtonHidden() {
        Espresso.onView(`is`(fragment.addContactButton))
                .check(matches(withEffectiveVisibility(GONE)))
    }

    private fun checkSubmitDisabled() {
        Espresso.onView(`is`(fragment.createTransferContinueButton))
                .check(CustomViewAssertions.isDisabled())
    }

    private fun checkSubmitEnabled() {
        Espresso.onView(`is`(fragment.createTransferContinueButton))
                .check(CustomViewAssertions.isEnabled())
    }

    private fun checkToolbarSearchActive() {
        assertTrue(fragment.searchItem.isActionViewExpanded)
    }

    private fun checkToolbarSearchInactive() {
        assertFalse(fragment.searchItem.isActionViewExpanded)
    }

}