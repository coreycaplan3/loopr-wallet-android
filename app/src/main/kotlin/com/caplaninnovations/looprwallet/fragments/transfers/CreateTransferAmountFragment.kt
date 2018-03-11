package com.caplaninnovations.looprwallet.fragments.transfers

import android.os.Bundle
import android.view.View
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.fragments.BaseFragment
import com.caplaninnovations.looprwallet.models.user.Contact

/**
 * Created by Corey Caplan on 2/18/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
class CreateTransferAmountFragment : BaseFragment() {

    companion object {
        val TAG: String = CreateTransferAmountFragment::class.java.simpleName

        private const val KEY_RECIPIENT_ADDRESS = "_RECIPIENT_ADDRESS"

        fun createInstance(recipientAddress: String): CreateTransferAmountFragment {
            return CreateTransferAmountFragment().apply {
                arguments = Bundle().apply { putString(KEY_RECIPIENT_ADDRESS, recipientAddress) }
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_create_transfer_amount

    private val recipientAddress: String by lazy {
        arguments!!.getString(KEY_RECIPIENT_ADDRESS)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactQuery = (activity as? BaseActivity)?.realm?.where(Contact::class.java)
        val contact = contactQuery?.equalTo("address", recipientAddress)?.findFirst()

        toolbar?.title = contact?.name

    }

}