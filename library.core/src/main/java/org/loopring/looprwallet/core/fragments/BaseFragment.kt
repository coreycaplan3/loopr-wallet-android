package org.loopring.looprwallet.core.fragments

import android.arch.lifecycle.Lifecycle.Event
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.*
import android.support.design.widget.CoordinatorLayout.*
import android.support.transition.TransitionSet
import android.support.transition.Visibility
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.*
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.delegates.BaseFragmentToolbarDelegate
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.models.android.architecture.FragmentViewLifecycleOwner
import org.loopring.looprwallet.core.transitions.FloatingActionButtonTransition
import org.loopring.looprwallet.core.utilities.ApplicationUtility.dimen
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.validators.BaseValidator
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.core.views.LooprAppBarLayout
import org.loopring.looprwallet.core.wallet.WalletClient
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * Created by Corey on 1/14/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
abstract class BaseFragment : Fragment(), ViewLifecycleFragment {

    interface OnToolbarSetupListener {

        fun onToolbarSetup(toolbar: Toolbar)
    }

    /**
     * The layout resource used to inflate this fragment's view
     */
    abstract val layoutResource: Int

    @Inject
    lateinit var walletClient: WalletClient

    val isToolbarCollapseEnabled: Boolean
        get() = toolbarDelegate?.isToolbarCollapseEnabled == true

    @DrawableRes
    open var navigationIcon: Int = R.drawable.ic_arrow_back_white_24dp

    val appbarLayout: AppBarLayout?
        get() = toolbarDelegate?.appbarLayout

    val toolbar: Toolbar?
        get() = toolbarDelegate?.toolbar

    var floatingActionButton: FloatingActionButton? = null
        private set

    var toolbarDelegate: BaseFragmentToolbarDelegate? = null

    private val fabTransitionName
        get() = "fab-transition-$tag"

    var validatorList: List<BaseValidator>? = null
        set(value) {
            field = value
            onFormChanged()
        }

    /**
     * True if the "up navigation" button should be shown in the toolbar or false otherwise
     */
    var isUpNavigationEnabled = true
        protected set

    override var fragmentViewLifecycleFragment: FragmentViewLifecycleOwner? = null

    init {
        injectComponents()
    }

    private fun injectComponents() {
        coreLooprComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarDelegate = BaseFragmentToolbarDelegate(
                savedInstanceState = savedInstanceState,
                isToolbarCollapseEnabled = false,
                fragment = this
        )

        allowEnterTransitionOverlap = false
        allowReturnTransitionOverlap = false

        enterTransition = TransitionSet()
                .addTransition(FloatingActionButtonTransition()
                        .addMode(Visibility.MODE_IN)
                        .addTarget(fabTransitionName)
                )

        exitTransition = TransitionSet()
                .addTransition(FloatingActionButtonTransition()
                        .addMode(Visibility.MODE_OUT)
                        .addTarget(fabTransitionName)
                )
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Make sure that the user has a wallet, if necessary
        (activity as? BaseActivity)?.checkWalletStatus()

        fragmentViewLifecycleFragment = FragmentViewLifecycleOwner().apply {
            lifecycle.handleLifecycleEvent(Event.ON_CREATE)
        }

        val fragmentView = inflater.inflate(layoutResource, container, false) as ViewGroup

        if (parentFragment == null) {
            // We are NOT in a child fragment
            toolbarDelegate?.setupAppbar(fragmentView)
            createFab(fragmentView)
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (parentFragment == null) {
            // We are NOT in a child fragment
            toolbarDelegate?.setupToolbarCollapsing()
            floatingActionButton?.let { initializeFloatingActionButton(it) }
        }
    }

    open fun createAppbarLayout(fragmentView: ViewGroup): AppBarLayout {
        return LooprAppBarLayout.createMain(fragmentView.context)
    }

    /**
     * Called when the [floatingActionButton] is safe to be initialized. This may include setting
     * its visibility, click listeners, icons, etc.
     *
     * The default implementation sets the fab's visibility to GONE (it assumes the fragment does
     * not need a fab).
     */
    open fun initializeFloatingActionButton(floatingActionButton: FloatingActionButton) {
        floatingActionButton.visibility = View.GONE
    }

    final override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {}

    final override fun onOptionsItemSelected(item: MenuItem?) = false

    open fun onShowKeyboard() {
        // Defaults to no-op
    }

    open fun onHideKeyboard() {
        // Defaults to no-op
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

    /**
     * Executes the given fragment transaction, pushing the given fragment to the front and saving
     * the old one (if present)
     */
    fun pushFragmentTransaction(fragment: BaseFragment, tag: String) {
        (activity as? BaseActivity)?.pushFragmentTransaction(fragment, tag)
    }

    private val viewModelList = arrayListOf<OfflineFirstViewModel<*, *>>()

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
            @StringRes progressMessage: Int,
            crossinline convertErrorToMessage: (Throwable) -> String
    ) {
        setupTransactionViewModel(viewModel, str(progressMessage), convertErrorToMessage)
    }

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
            } else if (progress?.isShowing == true) {
                progress.dismiss()
            }
        })

        viewModel.result.observeForDoubleSpend(this) {
            activity?.let {
                it.finish()
                it.startActivity(Intent(it, CoreLooprWalletApp.mainClass))
            }
        }

        viewModel.error.observeForDoubleSpend(this) {
            activity?.longToast(convertErrorToMessage(it))
        }
    }

    /**
     * Sets up the offline-first data observer by updating the [adapter] and removing the
     * [viewModel]'s data observer (since the [adapter] registers its own data observer).
     *
     * @param viewModel The view model whose data observer will be unregistered
     * @param adapter The adapter whose data will be updated
     * @param data The data that will be bound to the [adapter]
     */
    protected fun <T : RealmModel> setupOfflineFirstDataObserverForAdapter(
            viewModel: OfflineFirstViewModel<*, *>?,
            adapter: BaseRealmAdapter<T>,
            data: OrderedRealmCollection<T>
    ) {
//        viewModel?.removeDataObserver(this) // TODO fix me
        adapter.updateData(data)
    }

    /**
     * This method is responsible for two things:
     *
     * Sets up the [OfflineFirstViewModel] to handle state changes in data. This method will then
     * show/hide the [BaseFragment]'s [progressBar] accordingly.
     *
     * This method registers the *errorObserver* for this [OfflineFirstViewModel] and shows an error
     * snackbar on error. If an error occurs, an indefinite or long snackbar will appear, depending
     * on whether the user has any valid data. The criteria for an indefinite vs long snackbar are
     * as follows:
     * - A ViewModel with valid data will show a *long* snackbar.
     * - A ViewModel **without** valid data will show an *indefinite* snackbar.
     *
     * @param viewModel The [OfflineFirstViewModel] that will be registered with this fragment.
     * @param refreshLayout The refresh layout that shows the refresh drawable when the *ViewModel*
     * is loading or null otherwise
     */
    protected fun setupOfflineFirstStateAndErrorObserver(
            viewModel: OfflineFirstViewModel<*, *>?,
            refreshLayout: SwipeRefreshLayout?
    ) {
        if (viewModel != null && viewModelList.none { it === viewModel }) {
            // If there are no viewModels in the list equal to this reference, add it
            viewModelList.add(viewModel)
        }

        // REFRESH LAYOUT
        refreshLayout?.apply {
            val theme = context.theme
            this.setOnRefreshListener {
                isRefreshing = true
                refreshAllOfflineFirstViewModels()
            }
            this.setColorSchemeResources(
                    theme.getResourceIdFromAttrId(R.attr.colorAccent),
                    theme.getResourceIdFromAttrId(R.attr.colorPrimary),
                    theme.getResourceIdFromAttrId(R.attr.colorAccent),
                    theme.getResourceIdFromAttrId(R.attr.colorPrimary)
            )
        }

        val lifecycleOwner = fragmentViewLifecycleFragment ?: return

        // STATE OBSERVER
        viewModel?.addCurrentStateObserver(lifecycleOwner) {
            val isRefreshing = when {
                viewModelList.any(OfflineFirstViewModel<*, *>::isLoading) -> true
                else -> false
            }

            if (refreshLayout?.isRefreshing != isRefreshing) {
                refreshLayout?.isRefreshing = isRefreshing
            }

            if (viewModelList.all { it.hasValidData() }) {
                // If all of the viewModels have valid data, dismiss the snackbar and nullify it
                indefiniteSnackbar?.dismiss()
                indefiniteSnackbar = null
            }

            onOfflineFirstStateChange(viewModel, it)
        }

        // ERROR OBSERVER
        viewModel?.addErrorObserver(lifecycleOwner) {
            val snackbar = indefiniteSnackbar
            if (snackbar == null || !snackbar.isShownOrQueued) {
                // If we aren't already showing the refreshAll snackbar, let's show it
                when {
                    viewModel.hasValidData() ->
                        view?.longSnackbarWithAction(it.errorMessage, R.string.reload) { refreshAllOfflineFirstViewModels() }
                    else -> {
                        // This snackbar should be indefinite because we NEED the data.
                        indefiniteSnackbar = view?.indefiniteSnackbarWithAction(it.errorMessage, R.string.reload) { refreshAllOfflineFirstViewModels() }
                                ?.addCallback(snackbarCallbackRemoverListener)
                    }
                }
            }
        }
    }

    /**
     * Refreshes all viewModels in the [viewModelList] that were added via calls to
     * [setupOfflineFirstDataObserverForAdapter].
     */
    protected fun refreshAllOfflineFirstViewModels() {
        viewModelList.forEach(OfflineFirstViewModel<*, *>::refresh)
    }

    /**
     * @param viewModel The [OfflineFirstViewModel] whose state changed
     * @param state The new state of this [OfflineFirstViewModel]
     */
    protected open fun onOfflineFirstStateChange(viewModel: OfflineFirstViewModel<*, *>, state: Int) {
        // NO OP
    }

    // MARK - START TOOLBAR FUNCTIONS

    fun enableToolbarCollapsing() {
        toolbarDelegate?.enableToolbarCollapsing()
    }

    fun disableToolbarCollapsing() {
        toolbarDelegate?.disableToolbarCollapsing()
    }

    // MARK - END TOOLBAR FUNCTIONS

    var indefiniteSnackbar: Snackbar? = null

    override fun onStart() {
        super.onStart()
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Event.ON_RESUME)
    }

    override fun onPause() {
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        validatorList?.forEach { it.destroy() }
        (activity as? BaseActivity)?.setSupportActionBar(null)

        fragmentViewLifecycleFragment?.lifecycle?.handleLifecycleEvent(Event.ON_DESTROY)
        fragmentViewLifecycleFragment = null

        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        toolbarDelegate?.onSaveInstanceState(outState)
    }

    // MARK - Private Methods

    private fun createFab(fragmentView: ViewGroup) {
        floatingActionButton = FloatingActionButton(fragmentView.context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                val margin = dimen(R.dimen.standard_margin).roundToInt()
                topMargin = margin
                bottomMargin = margin
                leftMargin = margin
                rightMargin = margin
                compatElevation = dimen(R.dimen.fab_elevation)
                gravity = Gravity.BOTTOM or Gravity.END
            }
            id = R.id.floatingActionButton
            ViewCompat.setTransitionName(this, fabTransitionName)
            fragmentView.addView(this)
        }
    }

    protected val snackbarCallbackRemoverListener = object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            indefiniteSnackbar?.removeCallback(this)
            indefiniteSnackbar = null
        }
    }

}
