package com.caplaninnovations.looprwallet.dialogs

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.*
import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.extensions.equalTo
import com.caplaninnovations.looprwallet.extensions.removeAllListenersAndClose
import com.caplaninnovations.looprwallet.models.user.Contact
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.dialog_create_contact.*
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.withTimeout
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Corey on 3/30/2018.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 */
class CreateContactDialogTest : BaseDaggerFragmentTest<CreateContactDialog>() {

    override val fragment = CreateContactDialog.create(null)

    override val tag = CreateContactDialog.TAG

    val name = "Loopr Contact"

    @Test
    fun createContact() = runBlockingUiCode {
        val realm = createRealm()

        Espresso.onView(`is`(fragment.contactAddressEditText))
                .perform(typeText(name), closeSoftKeyboard())

        Espresso.onView(`is`(fragment.contactAddressEditText))
                .perform(typeText(address), closeSoftKeyboard())

        val deferred = CompletableDeferred<Boolean>()

        val contact = realm.where<Contact>()
                .equalTo(Contact::address, address)
                .findFirstAsync()

        contact.addChangeListener { c: Contact, _ ->
            when {
                c.address == address && c.name == name -> deferred.complete(true)
                else -> deferred.complete(false)
            }
        }

        Espresso.onView(`is`(fragment.createContactButton))
                .perform(click())

        val isComplete = withTimeout(500L) { deferred.await() }

        assertTrue(isComplete)

        realm.removeAllListenersAndClose()
    }

}