package org.loopring.looprwallet.contacts.adapters.contacts

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caplaninnovations.looprwallet.R
import org.loopring.looprwallet.core.extensions.getResourceIdFromAttrId
import com.caplaninnovations.looprwallet.models.user.Contact
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_contact.*

/**
 * Created by Corey Caplan on 3/11/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class ContactsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @Suppress("deprecation")
    fun bind(item: Contact, selectedContactAddress: String?, onContactSelected: (Int) -> Unit) {
        contactNameLabel.text = item.name
        contactAddressLabel.text = item.address

        val backgroundColorResource = if (selectedContactAddress == item.address) {
            itemView.context.theme.getResourceIdFromAttrId(R.attr.colorPrimary)
        } else {
            itemView.context.theme.getResourceIdFromAttrId(R.attr.cardBackgroundColor)
        }

        val backgroundColor = itemView.context.resources.getColor(backgroundColorResource)
        itemView.setBackgroundColor(backgroundColor)
        itemView.setOnClickListener { onContactSelected(adapterPosition) }
    }

}