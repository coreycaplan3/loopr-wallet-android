package com.caplaninnovations.looprwallet.viewmodels

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import com.caplaninnovations.looprwallet.R
import com.caplaninnovations.looprwallet.extensions.loge
import com.caplaninnovations.looprwallet.extensions.logw
import com.caplaninnovations.looprwallet.models.error.ErrorTypes
import com.caplaninnovations.looprwallet.models.error.LooprError
import com.caplaninnovations.looprwallet.models.error.UnknownLooprError
import com.caplaninnovations.looprwallet.repositories.BaseRepository
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.kotlin.isValid
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*

/**
 * Created by Corey Caplan on 3/14/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: A [ViewModel] class whose responsibility is simplifying state and data access
 * for the UI. This class uses the repository to retrieve data initially and gets fresh data from the
 * network.
 *
 * @param T The type of data this [ViewModel] is responsible for retrieving
 * @param U The type of object used for querying and retrieving [T] (a primitive/object that stores
 * the predicates for data retrieval).
 */
abstract class OfflineFirstViewModel<T, in U> : ViewModel() {

    companion object {

        /**
         * No data is being loaded but we have fresh data
         */
        const val STATE_IDLE_HAVE_DATA = 1

        /**
         * No data is being loaded, but we're in an empty state
         */
        const val STATE_IDLE_EMPTY = 2

        /**
         * We're loading from a non-empty state. This could occur during a refresh or after the
         * repository found old results, but we still have a request loading.
         */
        const val STATE_LOADING_HAVE_DATA = 3

        /**
         * We're loading from an empty state
         */
        const val STATE_LOADING_EMPTY = 4

        /**
         * The default time to wait until refreshing again, which is 30 seconds.
         */
        const val DEFAULT_WAIT_TIME_MILLIS = 1000 * 30L
    }

    protected abstract val repository: BaseRepository<*>

    private var mParameter: U? = null

    /**
     * This variable is setup in the call to [initializeData]
     */
    private var mLiveData: LiveData<T>? = null

    /**
     * The data currently being monitored by this [ViewModel] or null if:
     * - There is no [LiveData] being observed
     * - The value of the data is null or invalid
     */
    val data: T?
        get() {
            val value = mLiveData?.value
            return when {
                isDataValid(value) -> value
                else -> null
            }
        }

    private val mError = MutableLiveData<LooprError>()

    private val mCurrentState = MutableLiveData<Int>().apply {
        // We have no data yet, so the state is set to be empty and idle.
        this.value = STATE_IDLE_EMPTY
    }

    val currentState: Int
        get() {
            return mCurrentState.value!!
        }

    /**
     * True if there is currently a network operation running or false otherwise. This variable
     * is used to distinguish between the repository being done loading vs. the network
     */
    @Volatile
    private var mIsNetworkOperationRunning = false

    fun addDataObserver(owner: LifecycleOwner, onChange: (T) -> Unit) {
        mLiveData?.observe(owner, Observer {
            if (it != null && isDataValid(it)) onChange(it)
        })
    }

    /**
     * An mError that may or may not have occurred. Should be used in conjunction with
     * *LiveData.observeForDoubleSpend* so you don't display the same mError *twice* to a user.
     */
    fun addErrorObserver(owner: LifecycleOwner, onChange: (LooprError) -> Unit) {
        mError.observe(owner, Observer {
            if (it != null) onChange(it)
        })
    }

    /**
     * Registers an observer and listens for changes in the state of this [ViewModel].
     *
     * @see STATE_IDLE_HAVE_DATA
     * @see STATE_IDLE_EMPTY
     * @see STATE_LOADING_HAVE_DATA
     * @see STATE_LOADING_EMPTY
     */
    fun addCurrentStateObserver(lifecycleOwner: LifecycleOwner, onChange: (Int) -> Unit) {
        mCurrentState.observe(lifecycleOwner, Observer {
            if (it != null) onChange(it)
        })
    }

    /**
     * @return True if there is currently **no** data loading or false if there is
     */
    fun isIdle(): Boolean {
        return mCurrentState.value == STATE_IDLE_EMPTY || mCurrentState.value == STATE_IDLE_HAVE_DATA
    }

    /**
     * @return True if there is currently **no** data loading or false if there is
     */
    fun hasValidData(): Boolean {
        return mCurrentState.value == STATE_IDLE_HAVE_DATA || mCurrentState.value == STATE_LOADING_HAVE_DATA
    }

    /**
     * @return True if there is currently data loading or false if there is **not**
     */
    fun isLoading(): Boolean {
        return mCurrentState.value == STATE_LOADING_EMPTY || mCurrentState.value == STATE_LOADING_HAVE_DATA
    }

    /**
     * Refreshes the data for this [ViewModel] by pinging the network for fresh data.
     *
     * **NOTE** The refresh may **NOT** actually occur if one of the following is true:
     * - There is already a refresh operation running
     * - A refresh recently occurred, so pinging the network again would be pointless and likely be
     * a waste of resources
     */
    @Synchronized
    fun refresh() = launch(UI) {
        val parameter = mParameter
        if (parameter == null) {
            logw("Parameter is null somehow...", IllegalStateException())
            return@launch
        }

        if (!mIsNetworkOperationRunning && isRefreshNecessary()) {
            // We don't have a network operation already running and a refresh is necessary
            mCurrentState.value = getCurrentLoadingState(mLiveData?.value)
            handleNetworkRequest(parameter).await()
        } else {
            mCurrentState.value = getCurrentIdleState(mLiveData?.value)
        }
    }

    // MARK - Protected Methods

    /**
     * This method initializes the offline-first data pipeline for the UI.
     *
     * Callers of this method should be sure not to overwrite this object's [LiveData]
     * unnecessarily every time a [ViewModel] is created.
     *
     * After resetting this [ViewModel]'s state, this method makes calls to
     * [getLiveDataFromRepository]. Then, this method calls [getDataFromNetwork] to pull fresh data
     * from the network.
     *
     * @param owner The [LifecycleOwner] that will be tied to observing the [LiveData]
     * @param parameter The parameter that will be used passed into [getLiveDataFromRepository] and
     * [getDataFromNetwork] for querying the repository and network.
     * @param onChange The observer that will watch for non-null value changes with the created
     * [LiveData]
     */
    @Synchronized
    protected fun initializeData(owner: LifecycleOwner, parameter: U, onChange: (T) -> Unit) {

        val removeObserver = { liveData: LiveData<T> ->
            liveData.removeObservers(owner)
        }

        val addObserver = { liveData: LiveData<T> ->
            liveData.observe(owner, createObserver(onChange))
        }

        initializeDataInternal(parameter, removeObserver, addObserver)
    }

    /**
     * This observer is used solely with [foreverObserver] so we have a way to remember and remove
     * it when [initializeDataForever] is called.
     */
    private var foreverObserver: Observer<T>? = null

    /**
     * This method initializes the offline-first data pipeline for the UI.
     *
     * Callers of this method should be sure not to overwrite this object's [LiveData] unnecessarily
     * every time a [ViewModel] is created.
     *
     * This method makes calls to [getLiveDataFromRepository] after resetting old state. Right after,
     * this method calls [getDataFromNetwork] to get fresh data from the network.
     *
     * This method will make [LiveData] to observe changes forever.
     *
     * @param parameter The parameter that will be passed into [getLiveDataFromRepository] and
     * [getDataFromNetwork] for querying the repository and network.
     * @param onChange The observer that will watch for non-null value changes with the created
     * [LiveData].
     */
    @Synchronized
    protected fun initializeDataForever(parameter: U, onChange: (T) -> Unit) {

        val removeObserver = { liveData: LiveData<T> ->
            foreverObserver?.let { liveData.removeObserver(it) } ?: Unit
        }

        val addObserver = { liveData: LiveData<T> ->
            val observer = createObserver(onChange)
            liveData.observeForever(observer)
            foreverObserver = observer
        }

        initializeDataInternal(parameter, removeObserver, addObserver)
    }

    /**
     * This method should create the [LiveData] object that will be observed for this
     * [OfflineFirstViewModel]. This method is called internally inside [initializeData]. So,
     * subclasses must have any necessary parameters for creating this [LiveData] saved as field
     * variables.
     *
     * This method is called from the UI thread, so it **CANNOT** block!
     *
     * Examples of this object's creation could be:
     * - Creating an **asynchronous** [Realm] query that then makes a call to *asLiveData*.
     *
     * @param parameter The parameter passed into [initializeData] for querying the repository.
     */
    protected abstract fun getLiveDataFromRepository(parameter: U): LiveData<T>

    /**
     * This method is called in [initializeData] after [liveData] has been initialized (it is
     * **not** null).
     *
     * The default implementation of this method does nothing.
     *
     * @param liveData The [LiveData] object that was initialized
     */
    protected open fun onLiveDataInitialized(liveData: LiveData<T>) {
    }

    /**
     * Performs the network request for this ViewModel. This request should not block. Instead, it
     * returns a [Deferred] object (a promise).
     *
     * @param parameter The parameter passed into [initializeData] for querying the network.
     */
    protected abstract fun getDataFromNetwork(parameter: U): Deferred<T>

    /**
     * Adds the given [data] to the repository so it can be cached and used offline.
     *
     * @param data The fresh data that was just retrieved from the network.
     */
    protected abstract fun addNetworkDataToRepository(data: T)

    /**
     * Checks if the parameter for this [OfflineFirstViewModel] is the same as the parameter being
     * requested by a call to [initializeData]. Reason being, we don't want to overwrite [LiveData]
     * unnecessarily since it may already have data loaded. Otherwise, we would force a (wasted)
     * reload from the database and network.
     *
     * A common example of this use-case is when a fragment/activity is destroyed and recreated
     * during a configuration change. We may naively re-initialize everything, even though we
     * already have data.
     *
     * If we don't reinitialize the data, we still register new observers internally.
     *
     * @param oldParameter The old parameter that was being used by this [OfflineFirstViewModel].
     * Can be null.
     * @param newParameter The new parameter that was just passed into this [OfflineFirstViewModel].
     *
     * @return True if they're equal (and we should **NOT** reinitialize the data pipeline) or
     * false if they are **NOT** equal. A value of true **OR** false will still register any
     * observers on the list data.
     */
    protected open fun isPredicatesEqual(oldParameter: U?, newParameter: U): Boolean {
        return oldParameter == newParameter
    }

    /**
     * @return True if a refresh is necessary or false if it's not. A few examples of this method's
     * logic are the following:
     * - Check when the last refresh occurred for this data to see if the user is syncing too often.
     * - The user is trying to refreshing faster than blocks are confirmed.
     */
    protected abstract fun isRefreshNecessary(): Boolean

    /**
     * Checks if a refresh is necessary, based on the [lastRefreshTime] and the [waitTime] between
     * refreshes.
     *
     * @param lastRefreshTime The last time at which data was refreshed.
     * @param waitTime The wait time between refreshes.
     * @return True if a refresh is necessary or false if it's not.
     */
    protected fun isDefaultRefreshNecessary(lastRefreshTime: Long, waitTime: Long): Boolean {
        return lastRefreshTime + waitTime < Date().time
    }

    /**
     * Checks if the provided [data] is valid. Meaning data is **NOT** null and is ready to be used
     * by a view.
     *
     * Here are some examples of *invalid* data:
     * - *RealmObject*s that are loaded asynchronously and have **NOT** finished loading yet.
     *
     * @param data The data to be checked for whether or not it's valid.
     */
    protected open fun isDataValid(data: T?) = when (data) {
        null -> false
        is RealmModel -> data.isValid()
        is RealmResults<*> -> data.isValid
        else -> throw NotImplementedError("Default implementation does not work!")
    }

    /**
     * Checks if the provided [data] is empty. Meaning data is **NOT** null and not "empty".
     *
     * Here are some examples of *empty* data:
     * - Lists that contain **NO** items
     * - *RealmObject*s that are loaded asynchronously and have **NOT** finished loading yet
     *
     * @param data The data to be checked for whether or not it's valid.
     */
    protected open fun isDataEmpty(data: T?) = when (data) {
        null -> false
        is RealmModel -> data.isValid()
        is RealmResults<*> -> data.isValid
        else -> throw NotImplementedError("Default implementation does not work!")
    }

    /**
     * This method is simply a wrapper around [onCleared], since it has *protected* access.
     */
    fun clear() = this.onCleared()

    override fun onCleared() {
        super.onCleared()

        foreverObserver?.let { mLiveData?.removeObserver(it) }
        repository.clear()
    }

    // MARK - Private Methods

    /**
     * Creates an observer for watching changes in [T]'s value.
     *
     * @param onChange The function to be called when [T] changes.
     */
    private inline fun createObserver(crossinline onChange: (T) -> Unit): Observer<T> {
        return Observer {
            if (mIsNetworkOperationRunning) {
                // There is a network operation running, so we're still loading
                mCurrentState.value = getCurrentLoadingState(it)
            } else {
                // There is no network operation running, so we must be idling
                mCurrentState.value = getCurrentIdleState(it)
            }

            if (it != null && isDataValid(it)) {
                onChange(it)
            } else {
                loge("Invalid data! {state: $mCurrentState, " +
                        "isNetworkRunning: $mIsNetworkOperationRunning}", IllegalStateException())
            }
        }
    }

    /**
     * Initializes the offline-first data pipeline.
     */
    private inline fun initializeDataInternal(
            parameter: U,
            removeObserver: (LiveData<T>) -> Unit,
            addObserver: (LiveData<T>) -> Unit
    ) {
        // Check if we're re-initializing the same thing again
        val oldLiveData = mLiveData
        if (oldLiveData != null && isPredicatesEqual(mParameter, parameter)) {
            addObserver(oldLiveData)
            return
        }

        // Remove old observers
        mLiveData?.let { removeObserver(it) }

        // Reinitialize the state
        mCurrentState.value = STATE_LOADING_EMPTY

        // Reinitialize data, observers, and notify via the call to onLiveDataInitialized
        this.mParameter = parameter
        val liveData = getLiveDataFromRepository(parameter)
        this.mLiveData = liveData
        addObserver(liveData)
        onLiveDataInitialized(liveData)

        // Ping the network for fresh data
        refresh()
    }

    /**
     * Asynchronously handles network requests (by calling [getDataFromNetwork] and keeps track of
     * and updates the following:
     * - [mLiveData] (indirectly, by adding the fresh data to the repository)
     * - [mError]
     * - [mCurrentState]
     * - [mIsNetworkOperationRunning]
     *
     * @param parameter The parameter used for querying the network. It's passed into
     * [getDataFromNetwork].
     */
    private fun handleNetworkRequest(parameter: U) = async {
        try {
            mIsNetworkOperationRunning = true
            val response = getDataFromNetwork(parameter)
            val data = response.await()
            mIsNetworkOperationRunning = false

            // Update the current state and add the data to Realm
            mCurrentState.postValue(STATE_IDLE_HAVE_DATA)
            addNetworkDataToRepository(data)
        } catch (exception: Exception) {
            val looprError = when (exception) {
                is HttpException -> {
                    loge("Network communication addErrorObserver: ", exception)
                    if (exception.code() >= 500) {
                        LooprError(R.string.error_server_error, ErrorTypes.SERVER_ERROR)
                    } else {
                        LooprError(R.string.error_http_communication, ErrorTypes.SERVER_COMMUNICATION_ERROR)
                    }
                }
                is IOException -> {
                    LooprError(R.string.error_no_connection, ErrorTypes.NO_CONNECTION)
                }
                else -> {
                    loge("Unknown error: ", exception)
                    UnknownLooprError()
                }
            }

            // Update the current state and mError

            val state = getCurrentIdleState(mLiveData?.value)
            mCurrentState.postValue(state)
            mError.postValue(looprError)
        }
    }

    /**
     * Gets the current state, assuming we're idling
     */
    private fun getCurrentIdleState(currentData: T?) =
            if (!isDataEmpty(currentData)) STATE_IDLE_HAVE_DATA
            else STATE_IDLE_EMPTY

    /**
     * Gets the current state, assuming we're loading
     */
    private fun getCurrentLoadingState(currentData: T?): Int =
            if (!isDataEmpty(currentData)) STATE_LOADING_HAVE_DATA
            else STATE_LOADING_EMPTY
}