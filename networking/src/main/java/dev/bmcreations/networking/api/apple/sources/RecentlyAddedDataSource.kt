package dev.bmcreations.networking.api.apple.sources

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import dev.bmcreations.guacamole.models.apple.RecentlyAddedEntity
import dev.bmcreations.networking.NetworkState
import dev.bmcreations.networking.Outcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyAddedDataFactory : DataSource.Factory<Int, RecentlyAddedEntity>() {

    val mutableLiveData: MutableLiveData<RecentlyAddedDataSource> = MutableLiveData()

    private var source: LibrarySource? = null

    fun provideLibrarySource(source: LibrarySource) {
        this.source = source
    }

    override fun create(): DataSource<Int, RecentlyAddedEntity> {
        val feedDataSource = RecentlyAddedDataSource().apply {
            source = this@RecentlyAddedDataFactory.source
        }
        mutableLiveData.postValue(feedDataSource)
        return feedDataSource
    }
}


class RecentlyAddedDataSource: CoroutineScope by CoroutineScope(Dispatchers.IO), PageKeyedDataSource<Int, RecentlyAddedEntity>() {

    var source: LibrarySource? = null

    val networkState = MutableLiveData<NetworkState>()
    private val initialLoading = MutableLiveData<NetworkState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, RecentlyAddedEntity>
    ) {
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)

        launch {
            when (val ret = source?.getUserRecentlyAdded(limit = params.requestedLoadSize)) {
                is Outcome.Success -> {
                    callback.onResult(ret.data?.data ?: emptyList(), null, ret.data?.nextOffset())
                    initialLoading.postValue(NetworkState.LOADED)
                    networkState.postValue(NetworkState.LOADED)
                }
                is Outcome.Failure -> {
                    initialLoading.postValue(
                        NetworkState(
                            NetworkState.Status.FAILED,
                            ret.e.localizedMessage
                        )
                    )
                    networkState.postValue(
                        NetworkState(
                            NetworkState.Status.FAILED,
                            ret.e.localizedMessage
                        )
                    )
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RecentlyAddedEntity>) {
        networkState.postValue(NetworkState.LOADING)
        launch {
            when (val ret = source?.getUserRecentlyAdded(limit = params.requestedLoadSize, offset = params.key)) {
                is Outcome.Success -> {
                    val next = ret.data?.nextOffset()

                    callback.onResult(ret.data?.data ?: emptyList(), next)
                    networkState.postValue(NetworkState.LOADED)

                }
                is Outcome.Failure -> networkState.postValue(
                    NetworkState(
                        NetworkState.Status.FAILED,
                        ret.e.localizedMessage
                    )
                )
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RecentlyAddedEntity>) = Unit
}
