package org.loopring.looprwallet.core.presenters

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.SearchView
import android.view.MenuItem
import org.loopring.looprwallet.core.animations.ToolbarToSearchAnimation
import org.loopring.looprwallet.core.extensions.logd
import org.loopring.looprwallet.core.extensions.weakReference
import org.loopring.looprwallet.core.fragments.BaseFragment

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

    var searchQuery: String? = null

    private val baseFragment by weakReference(baseFragment)
    private val listener by weakReference(listener)

    init {
        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
    }

    fun setupSearchView(searchItem: MenuItem, searchView: SearchView) {

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
                if (wasInitialized) {
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
                return true
            }
        }

    private val expandListener
        get() = object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                logd("Collapsing MenuItem...")

                searchQuery = null

                listener?.onSearchItemCollapsed()

                if (item.isActionViewExpanded) {
                    baseFragment?.let {
                        ToolbarToSearchAnimation.animateToToolbar(it, numberOfVisibleMenuItems, containsOverflowMenu)
                    }
                }
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Called when SearchView is expanding
                logd("Expanding MenuItem...")
                searchQuery = ""

                listener?.onSearchItemExpanded()

                baseFragment?.let {
                    ToolbarToSearchAnimation.animateToSearch(it, numberOfVisibleMenuItems, containsOverflowMenu)
                }
                return true
            }
        }
}