package com.caplaninnovations.looprwallet.dialogs

import com.caplaninnovations.looprwallet.dagger.BaseDaggerFragmentTest
import com.caplaninnovations.looprwallet.extensions.removeAllListenersAndClose
import com.caplaninnovations.looprwallet.models.user.Contact
import io.realm.RealmChangeListener
import org.junit.Test

/**
 * Created by Corey on 3/30/2018.
 *
 *
 * Project: loopr-wallet-android
 *
 *
 * Purpose of Class:
 */
class CreateContactDialogTest : BaseDaggerFragmentTest<CreateContactDialog>() {

    override val fragment = CreateContactDialog.create(null)

    override val tag = CreateContactDialog.TAG

    var isContactCreated = false

    val listener = RealmChangeListener<Contact> {
        isContactCreated = it.isValid
    }

    @Test
    fun createContact() {
        val realm = createRealm()

        // TODO use listener and test creating a contact

        realm.removeAllListenersAndClose()
    }

}