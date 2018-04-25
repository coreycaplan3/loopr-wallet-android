package org.loopring.looprwallet.core.fragments

import android.arch.lifecycle.Lifecycle.Event
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.*
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CoordinatorLayout.LayoutParams
import android.support.transition.TransitionSet
import android.support.transition.Visibility
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ProgressBar
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.activities.BaseActivity
import org.loopring.looprwallet.core.activities.SettingsActivity
import org.loopring.looprwallet.core.adapters.BaseRealmAdapter
import org.loopring.looprwallet.core.application.CoreLooprWalletApp
import org.loopring.looprwallet.core.dagger.coreLooprComponent
import org.loopring.looprwallet.core.extensions.*
import org.loopring.looprwallet.core.models.android.architecture.FragmentViewLifecycleOwner
import org.loopring.looprwallet.core.transitions.FloatingActionButtonTransition
import org.loopring.looprwallet.core.utilities.ApplicationUtility
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.core.utilities.ViewUtility
import org.loopring.looprwallet.core.validators.BaseValidator
import org.loopring.looprwallet.core.viewmodels.OfflineFirstViewModel
import org.loopring.looprwallet.core.viewmodels.TransactionViewModel
import org.loopring.looprwallet.core.wallet.WalletClient
import javax.inject.Inject


/**
 * Created by Corey on 1/14/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
abstract class BaseFragment : Fragment(), ViewLifecycleFragment {

    companion object {

        private const val KEY_IS_TOOLBAR_COLLAPSED = "_IS_TOOLBAR_COLLAPSED"
    }

    /**
     * The layout resource used to inflate this fragment's view
     */
    abstract val layoutResource: Int

    @Inject
    lateinit var walletClient: WalletClient

    var isToolbarCollapseEnabled: Boolean = false
        private set

    var appbarLayout: AppBarLayout? = null
        private set

    @DrawableRes
    open var navigationIcon: Int = R.drawable.ic_arrow_back_white_24dp

    var toolbar: Toolbar? = null
        private set

    var progressBar: ProgressBar? = null
        private set

    var floatingActionButton: FloatingActionButton? = null
        private set

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        allowEnterTransitionOverlap = false
        allowReturnTransitionOverlap = false

        enterTransition = TransitionSet()
                .addTransition(
                        FloatingActionButtonTransition()
                                .addMode(Visibility.MODE_IN)
                                .addTarget(fabTransitionName)
                )

        exitTransition = TransitionSet()
                .addTransition(
                        FloatingActionButtonTransition()
                                .addMode(Visibility.MODE_OUT)
                                .addTarget(fabTransitionName)
                )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreLooprComponent.inject(this)

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED) == true
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
            createAppbar(fragmentView, savedInstanceState)
            createProgressBar(fragmentView)
            createFab(fragmentView)
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (parentFragment == null) {
            // We are NOT in a child fragment
            setupAppbar()
            floatingActionButton?.let { initializeFloatingActionButton(it) }
        }
    }

    open fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return layoutInflater.inflate(R.layout.appbar_main, fragmentView, false) as? AppBarLayout
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

    override fun onOptionsItemSelected(item: MenuItem?) = when {
        item?.itemId == android.R.id.home -> {
            (activity as? BaseActivity)?.onBackPressed()
            true
        }
        item?.itemId == R.id.menuMainSettings -> {
            context?.let { startActivity(Intent(it, SettingsActivity::class.java)) }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

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

    /**
     * Executes the given fragment transaction, popping the current fragment from the front and
     * moving the next one in the stack to the front
     */
    fun popFragmentTransaction() {
        (activity as? BaseActivity)?.popFragmentTransaction()
    }

    /**
     * Enables the toolbar to be collapsed when scrolling
     */
    fun enableToolbarCollapsing() {
        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
        }

        val container = view?.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? LayoutParams)?.let {
            // The container is put underneath the toolbar since it is going to be moved out of the
            // way after scrolling
            logd("Setting container layout params to enable scrolling behavior...")
            it.topMargin = 0
            it.behavior = AppBarLayout.ScrollingViewBehavior()
            container.requestLayout()
        }

        isToolbarCollapseEnabled = true
    }

    /**
     * Disables the toolbar from being collapsed when scrolling
     */
    fun disableToolbarCollapsing() {
        (toolbar?.layoutParams as? AppBarLayout.LayoutParams)?.let {
            it.scrollFlags = 0
        }

        val view = view
        if (view == null) {
            loge("BaseView was null!", IllegalStateException())
            return
        }

        val container = view.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? LayoutParams)?.let {
            // The container is underneath the toolbar, so we must add margin so it is below it instead
            val topMarginResource = context?.theme?.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            if (topMarginResource != null) {
                it.topMargin = resources.getDimension(topMarginResource).toInt()
            }

            it.behavior = null
            container.requestLayout()
        }

        isToolbarCollapseEnabled = false
    }

    protected val viewModelList = arrayListOf<OfflineFirstViewModel<*, *>>()

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
     * @param refreshAll The function that will be called when all data needs to be refreshed. This
     * is called when an error occurs and the user opts into a refresh/retry.
     */
    protected fun setupOfflineFirstStateAndErrorObserver(
            viewModel: OfflineFirstViewModel<*, *>?,
            refreshLayout: SwipeRefreshLayout?
    ) {
        if (viewModel != null && viewModelList.none { it === viewModel }) {
            // If there are no viewModels in the list equal to this reference, add it
            viewModelList.add(viewModel)
        }

        // STATE OBSERVER
        viewModel?.addCurrentStateObserver(this) {
            refreshLayout?.isRefreshing = false
            progressBar?.visibility = when {
                viewModelList.any { it.isLoading() } -> View.VISIBLE
                else -> View.GONE
            }

            if (viewModelList.all { it.hasValidData() }) {
                // If all of the viewModels have valid data, dismiss the snackbar and nullify it
                indefiniteSnackbar?.dismiss()
                indefiniteSnackbar = null
            }

            onOfflineFirstStateChange(viewModel, it)
        }

        // ERROR OBSERVER
        viewModel?.addErrorObserver(this) {
            val snackbar = indefiniteSnackbar
            if (snackbar == null || !snackbar.isShownOrQueued) {
                // If we aren't already showing the refreshAll snackbar, let's show it
                val refreshAll = { _: View -> viewModelList.forEach(OfflineFirstViewModel<*, *>::refresh) }
                when {
                    viewModel.hasValidData() ->
                        view?.longSnackbarWithAction(it.errorMessage, R.string.reload, refreshAll)
                    else -> {
                        // This snackbar should be indefinite because we NEED the data.
                        indefiniteSnackbar = view?.indefiniteSnackbarWithAction(it.errorMessage, R.string.reload, refreshAll)
                                ?.addCallback(snackbarCallbackRemoverListener)
                    }
                }
            }
        }
    }

    /**
     * @param viewModel The [OfflineFirstViewModel] whose state changed
     * @param state The new state of this [OfflineFirstViewModel]
     */
    protected open fun onOfflineFirstStateChange(viewModel: OfflineFirstViewModel<*, *>, state: Int) {
        // NO OP
    }

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

        outState.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
    }

    // MARK - Private Methods

    private fun createAppbar(fragmentView: ViewGroup, savedInstanceState: Bundle?) {
        runBlocking {
            appbarLayout = async(CommonPool) { createAppbarLayout(fragmentView, savedInstanceState) }
                    .await()
        }

        fragmentView.addView(appbarLayout, 0)
        toolbar = appbarLayout?.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)

        ViewGroupCompat.setTransitionGroup(appbarLayout, true)

        val baseActivity = (activity as? BaseActivity)

        baseActivity?.setSupportActionBar(toolbar)

        if (isUpNavigationEnabled && baseActivity != null) {
            logi("Up navigation is enabled. Setting up...")
            toolbar?.navigationIcon = ViewUtility.getNavigationIcon(navigationIcon, baseActivity.theme)
            toolbar?.setNavigationContentDescription(R.string.content_description_navigation_icon)
        }
    }

    private fun createProgressBar(fragmentView: ViewGroup) {
        progressBar = fragmentView.inflate(R.layout.fragment_progress_bar) as ProgressBar

        if (fragmentView is CoordinatorLayout) {
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            params.leftMargin = ApplicationUtility.dimen(R.dimen.horizontal_margin).toInt()
            params.rightMargin = ApplicationUtility.dimen(R.dimen.horizontal_margin).toInt()
            params.anchorGravity = Gravity.BOTTOM
            params.anchorId = R.id.appbarLayout
            params.insetEdge = Gravity.TOP
            params.dodgeInsetEdges = Gravity.TOP
            fragmentView.addView(progressBar, 1, params)
        } else {
            fragmentView.addView(progressBar, 1)
        }
    }

    private fun createFab(fragmentView: ViewGroup) {
        floatingActionButton = (fragmentView.inflate(R.layout.floating_action_button) as FloatingActionButton)
                .apply {
                    ViewCompat.setTransitionName(this, fabTransitionName)
                    fragmentView.addView(this)
                }
    }

    private fun setupAppbar() {
        toolbar = (0 until (appbarLayout?.childCount ?: 0))
                .map { appbarLayout?.getChildAt(it) }
                .filterIsInstance<Toolbar>()
                .first()
        (activity as? BaseActivity)?.setSupportActionBar(toolbar)

        if (isToolbarCollapseEnabled) {
            enableToolbarCollapsing()
        } else {
            disableToolbarCollapsing()
        }
    }

    protected val snackbarCallbackRemoverListener = object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            indefiniteSnackbar?.removeCallback(this)
            indefiniteSnackbar = null
        }
    }

}
