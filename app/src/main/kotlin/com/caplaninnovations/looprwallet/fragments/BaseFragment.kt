package com.caplaninnovations.looprwallet.fragments

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.CoordinatorLayout.LayoutParams
import android.support.design.widget.FloatingActionButton
import android.support.transition.TransitionSet
import android.support.transition.Visibility
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewGroupCompat
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ProgressBar
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.activities.MainActivity
import com.caplaninnovations.looprwallet.activities.SettingsActivity
import com.caplaninnovations.looprwallet.application.LooprWalletApp
import com.caplaninnovations.looprwallet.extensions.*
import com.caplaninnovations.looprwallet.models.android.architecture.FragmentViewLifecycleOwner
import com.caplaninnovations.looprwallet.models.security.WalletClient
import com.caplaninnovations.looprwallet.transitions.FloatingActionButtonTransition
import com.caplaninnovations.looprwallet.utilities.ApplicationUtility
import com.caplaninnovations.looprwallet.validators.BaseValidator
import javax.inject.Inject


/**
 * Created by Corey on 1/14/2018.
 *
 * Project: LooprWallet
 *
 * Purpose of Class:
 *
 */
abstract class BaseFragment : Fragment() {

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

    private val fragmentLifeCycleOwner = FragmentViewLifecycleOwner()

    override fun getLifecycle(): Lifecycle = fragmentLifeCycleOwner.lifecycle

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        LooprWalletApp.application.looprProductionComponent

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

        // Make sure that the user has a wallet, if necessary
        (activity as? BaseActivity)?.checkWalletStatus()

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED) == true
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_CREATE)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (activity is MainActivity) {
            // Only the MainActivity can have the main_menu. This helps maintain the backstack in
            // case the user needs to recreate the back stack inside the Settings Activity.
            menu.clear()
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    open fun createAppbarLayout(fragmentView: ViewGroup, savedInstanceState: Bundle?): AppBarLayout? {
        return fragmentView.inflate(R.layout.appbar_main, false) as AppBarLayout?
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

    override fun onOptionsItemSelected(item: MenuItem?) = when {
        item?.itemId == android.R.id.home -> {
            (activity as? BaseActivity)?.onBackPressed()
            true
        }
        item?.itemId == R.id.menu_settings -> {
            context?.let { startActivity(Intent(it, SettingsActivity::class.java)) }
            true
        }
        item?.itemId == R.id.menu_settings_2 -> {
            /**
             * TODO This will be replaced down the road with a "select different wallet and
             * TODO remove wallet" feature
             */
            (activity as? BaseActivity)?.removeWalletCurrentWallet()
            true
        }
        else -> false
    }

    override fun onStart() {
        super.onStart()
        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_RESUME)
    }

    override fun onPause() {
        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {

        validatorList?.forEach { it.destroy() }
        (activity as? BaseActivity)?.setSupportActionBar(null)

        fragmentLifeCycleOwner.lifecycle.handleLifecycleEvent(Event.ON_DESTROY)

        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
    }

// MARK - Private Methods

    private fun createAppbar(fragmentView: ViewGroup, savedInstanceState: Bundle?) {
        appbarLayout = createAppbarLayout(fragmentView, savedInstanceState)
        fragmentView.addView(appbarLayout, 0)
        toolbar = appbarLayout?.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)

        ViewGroupCompat.setTransitionGroup(appbarLayout, true)

        val baseActivity = (activity as? BaseActivity)

        baseActivity?.setSupportActionBar(toolbar)
        baseActivity?.fragmentStackHistory?.let {
            if (it.isUpNavigationEnabled()) {
                logi("Up navigation is enabled...")
                toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
                toolbar?.setNavigationContentDescription(R.string.content_description_navigation_icon)
            }
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
        toolbar = (0..(appbarLayout?.childCount ?: 0))
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

}
