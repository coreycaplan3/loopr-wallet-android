package org.loopring.looprwallet.core.presenters

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.widget.ImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.loopring.looprwallet.core.R
import org.loopring.looprwallet.core.animations.ToolbarToSearchAnimation
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment
import org.loopring.looprwallet.core.utilities.ViewUtility
import java.lang.ref.WeakReference

/**
 * Created by Corey Caplan on 4/7/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To standardize the search bar's behavior across fragments by reusing this
 * presenter
 *
 * @param containsOverflowMenu True if this search view's menu contains an overflow menu. False if
 * it doesn't. This parameter is only used for animation purposes.
 * @param numberOfVisibleMenuItems The number of visible items in the menu that are **NOT**
 * overflow (the three vertical dots). This is only used for animation purposes.
 * @param baseFragment The fragment in which this presenter resides
 * @param savedInstanceState The saved instance state that's used to restore this presenter
 * @param listener The listener used for forward changes with the [SearchView] and menu to the
 * implementor
 */
class SearchViewPresenter(
        val containsOverflowMenu: Boolean,
        val numberOfVisibleMenuItems: Int,
        baseFragment: BaseFragment,
        savedInstanceState: Bundle?,
        listener: OnSearchViewChangeListener
) {

    /**
     * An interface for declaring a certain fragment is a *search fragment* for searching in the
     * toolbar.
     */
    interface SearchFragment {

        var searchViewPresenter: SearchViewPresenter
    }

    /**
     * A listener used for forwarding changes from the [SearchView] and search [MenuItem] to the
     * implementor.
     */
    interface OnSearchViewChangeListener {

        /**
         * Called when the query text *gains* focus
         */
        fun onQueryTextGainFocus()

        /**
         * Called when the query text changes
         *
         * @param searchQuery The current search query text
         */
        fun onQueryTextChangeListener(searchQuery: String)

        /**
         * Called when the search item is now visible and expanded
         */
        fun onSearchItemExpanded()

        /**
         * Called when the search item is now hidden and collapsed
         */
        fun onSearchItemCollapsed()
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "_SEARCH_QUERY"
    }

    /**
     * The current search query if searching is active or *null* if it's inactive
     */
    var searchQuery: String? = null
        private set

    private val baseFragment by weakReference(baseFragment)
    private val listener by weakReference(listener)

    private var searchItem = WeakReference<MenuItem>(null)

    init {
        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
    }

    val isExpanded
        get() = searchItem.get()?.isActionViewExpanded ?: false

    fun collapseSearchView() {
        searchItem.get()?.collapseActionView()
    }

    fun setupSearchView(searchItem: MenuItem, searchView: SearchView) {
        this.searchItem = WeakReference(searchItem)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                listener?.onQueryTextGainFocus()
            }
        }

        searchView.setOnQueryTextListener(queryTextChangeListener)

        searchItem.setOnActionExpandListener(expandListener)

        if (searchQuery != null) {
            Handler().postDelayed({
                // Wait fo the item and fragment to be setup
                searchItem.expandActionView()
                (searchItem.actionView as? SearchView)?.setQuery(searchQuery, false)
            }, 300)
        }

    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    // MARK - Private Methods

    private val queryTextChangeListener
        get() = object : SearchView.OnQueryTextListener {

            private var wasInitialized = false

            override fun onQueryTextChange(newText: String): Boolean {
                if (wasInitialized && !isCollapsing) {
                    searchQuery = newText
                } else {
                    wasInitialized = true
                }

                searchQuery?.let {
                    listener?.onQueryTextChangeListener(it)
                }

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                baseFragment?.view?.let { ViewUtility.closeKeyboard(it) }
                return true
            }
        }

    private var isCollapsing: Boolean = false
    private val expandListener = object : MenuItem.OnActionExpandListener {

        override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
            logd("Collapsing MenuItem...")

            isCollapsing = true
            searchQuery = null

            (baseFragment.activity as? BottomNavigationPresenter.BottomNavigation)
                    ?.bottomNavigationPresenter
                    ?.showBottomNavigation()

            listener.onSearchItemCollapsed()

            if (item.isActionViewExpanded) {
                ToolbarToSearchAnimation.animateToToolbar(baseFragment, numberOfVisibleMenuItems, containsOverflowMenu)
            }

            async<Unit>(UI) {
                delay(300)

                // Reset it after the collapse animation finishes
                baseFragment.toolbarDelegate?.resetOptionsMenu()
            }

            return true
        }

        override fun onMenuItemActionExpand(item: MenuItem): Boolean {
            // Called when SearchView is expanding
            logd("Expanding MenuItem...")
            isCollapsing = false
            searchQuery = ""

            (baseFragment.activity as? BottomNavigationPresenter.BottomNavigation)
                    ?.bottomNavigationPresenter
                    ?.hideBottomNavigation()

            item.actionView.findViewById<ImageView>(R.id.search_button).drawable?.let {
                DrawableCompat.setTint(it, Color.BLACK)
            }

            listener.onSearchItemExpanded()

            ToolbarToSearchAnimation.animateToSearch(baseFragment, numberOfVisibleMenuItems, containsOverflowMenu)
            return true
        }
    }

}