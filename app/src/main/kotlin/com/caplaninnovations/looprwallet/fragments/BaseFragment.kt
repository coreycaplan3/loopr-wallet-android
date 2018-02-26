package com.caplaninnovations.looprwallet.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewGroupCompat
import android.support.v7.widget.Toolbar
import android.view.*
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.activities.BaseActivity
import com.caplaninnovations.looprwallet.utilities.getResourceIdFromAttrId
import com.caplaninnovations.looprwallet.utilities.loge
import com.caplaninnovations.looprwallet.validators.BaseValidator

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

    var isToolbarCollapseEnabled: Boolean = false
        private set

    var appbarLayout: AppBarLayout? = null
        private set

    var toolbar: Toolbar? = null
        private set

    var validatorList: List<BaseValidator>? = null
        set(value) {
            field = value
            onFormChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isToolbarCollapseEnabled = savedInstanceState?.getBoolean(KEY_IS_TOOLBAR_COLLAPSED) == true
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(layoutResource, container, false) as ViewGroup

        if (parentFragment == null) {
            appbarLayout = createAppbarLayout(inflater, container, savedInstanceState)
            fragmentView.addView(appbarLayout)
            toolbar = appbarLayout?.findViewById(R.id.toolbar)

            ViewGroupCompat.setTransitionGroup(appbarLayout, true)

            val fragmentStack = (activity as? BaseActivity)?.fragmentStackHistory
            fragmentStack?.let {
                if (it.isUpNavigationEnabled()) {
                    toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
                    toolbar?.setNavigationContentDescription(R.string.content_description_navigation_icon)
                    toolbar?.setNavigationOnClickListener { popFragmentTransaction() }
                }
            }
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (parentFragment == null) {
            // We are NOT in a child fragment
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

            setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
    }

    open fun createAppbarLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): AppBarLayout? {
        return inflater.inflate(R.layout.appbar_main, container, false) as AppBarLayout?
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
            it.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        }

        val container = view?.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is put underneath the toolbar since it is going to be moved out of the
            // way after scrolling
            it.topMargin = 0
            it.behavior = AppBarLayout.ScrollingViewBehavior()
            container.layoutParams = it
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
        if(view == null) {
            loge("BaseView was null!", IllegalStateException())
            return
        }

        val container = view.findViewById<View>(R.id.fragmentContainer)
                ?: throw IllegalStateException("FragmentContainer cannot be null!")

        (container.layoutParams as? CoordinatorLayout.LayoutParams)?.let {
            // The container is underneath the toolbar, so we must add margin so it is below it instead
            val topMarginResource = context?.getResourceIdFromAttrId(android.R.attr.actionBarSize)
            if (topMarginResource != null) {
                it.topMargin = resources.getDimension(topMarginResource).toInt()
            }

            it.behavior = null
            container.layoutParams = it
            container.requestLayout()
        }

        isToolbarCollapseEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        /*
         * TODO This will be replaced down the road with a "select different wallet and remove
         * TODO wallet" feature
         */
        (activity as? BaseActivity)?.removeWalletCurrentWallet()
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        validatorList?.forEach { it.destroy() }
        (activity as? BaseActivity)?.setSupportActionBar(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IS_TOOLBAR_COLLAPSED, isToolbarCollapseEnabled)
    }

}
