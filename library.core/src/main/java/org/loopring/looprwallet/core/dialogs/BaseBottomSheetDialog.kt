package org.loopring.looprwallet.core.dialogs

import android.app.Dialog
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.longToast
import org.loopring.looprwallet.core.extensions.observeForDoubleSpend
import org.loopring.looprwallet.core.fragments.ViewLifecycleFragment
import org.loopring.looprwallet.core.models.android.architecture.FragmentViewLifecycleOwner
import org.loopring.looprwallet.core.validators.BaseValidator
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.core.wallet.WalletClient

/**
 * Created by Corey on 3/30/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A base instance of [BottomSheetDialogFragment] that allows for uniform
 * behavior for all of its subclasses.
 */
abstract class BaseBottomSheetDialog : BottomSheetDialogFragment(), ViewLifecycleFragment {

    abstract val layoutResource: Int

    var validatorList: List<BaseValidator>? = null
        set(value) {
            field = value
            onFormChanged()
        }

    lateinit var walletClient: WalletClient

    override var fragmentViewLifecycleFragment: FragmentViewLifecycleOwner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreLooprComponent.inject(this)
    }

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, this.theme)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentViewLifecycleFragment = FragmentViewLifecycleOwner().apply {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        return inflater.inflate(layoutResource, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? BottomSheetDialog)?.let { dialog ->

            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)

            dialog.setOnShowListener {
                val bottomSheet = dialog.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.peekHeight = 0
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    /**
     * Checks if all of the validators are valid. If [validatorList] is null, this method returns
     * true
     * @return True if all elements of [validatorList] are valid and the list is not null. Returns
     * false if at least one of them is invalid or the list is null.
     */
    fun isAllValidatorsValid(): Boolean {
        val validatorList = this.validatorList
        return validatorList != null && validatorList.all { it.isValid() }
    }

    /**
     * Propagates form changes to the rest of the UI, if necessary. This depends on the return
     * value of [isAllValidatorsValid].
     */
    open fun onFormChanged() {
        // Do nothing for now
    }

    // MARK - Protected Methods

    /**
     * Setups and standardizes the usage of [TransactionViewModel]. This includes the observers
     * used to watch for transactions and errors.
     *
     * @param viewModel The generator used for running transactions
     * @param progressMessage The message to be displayed by the activity's progressDialog when the
     * transaction is running.
     * @param convertErrorToMessage A function that takes a [Throwable] and converts it to a string
     * to be displayed in a *Toast*.
     */
    protected inline fun <T> setupTransactionViewModel(
            viewModel: TransactionViewModel<T>,
            progressMessage: String,
            crossinline convertErrorToMessage: (Throwable) -> String
    ) {
        viewModel.isTransactionRunning.observeForDoubleSpend(this, {
            val progress = (activity as? BaseActivity)?.progressDialog
            if (it) {
                progress?.setMessage(progressMessage)
                progress?.show()
            } else {
                if (progress?.isShowing == true) {
                    progress.dismiss()
                }
            }
        })

        viewModel.result.observeForDoubleSpend(this) {
            activity?.let {
                it.startActivity(Intent(it, CoreLooprWalletApp.mainClass))
                it.finish()
            }
        }

        viewModel.error.observeForDoubleSpend(this) {
            activity?.longToast(convertErrorToMessage(it))
        }
    }

    override fun onStart() {
        super.onStart()
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        validatorList?.forEach { it.destroy() }

        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fragmentViewLifecycleFragment = null

        super.onDestroyView()
    }

}