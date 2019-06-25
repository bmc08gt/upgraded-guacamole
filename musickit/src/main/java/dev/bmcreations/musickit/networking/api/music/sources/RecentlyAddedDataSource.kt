package dev.bmcreations.musickit.networking.api.music.sources

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import dev.bmcreations.musickit.networking.NetworkState
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import dev.bmcreations.musickit.networking.extensions.uiScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentlyAddedDataFactory : DataSource.Factory<Int, RecentlyAddedEntity>() {

    val mutableLiveData: MutableLiveData<RecentlyAddedDataSource> = MutableLiveData()

    private var repo: MusicRepository? = null

    fun provideMusicRepository(repo: MusicRepository) {
        this.repo = repo
    }

    override fun create(): DataSource<Int, RecentlyAddedEntity> {
        val feedDataSource = RecentlyAddedDataSource().apply {
            this.musicRepository = repo
        }
        mutableLiveData.postValue(feedDataSource)
        return feedDataSource
    }
}


class RecentlyAddedDataSource: PageKeyedDataSource<Int, RecentlyAddedEntity>() {

    var musicRepository: MusicRepository? = null

    val networkState = MutableLiveData<NetworkState>()
    private val initialLoading = MutableLiveData<NetworkState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, RecentlyAddedEntity>
    ) {
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)

        uiScope.launch(Dispatchers.IO) {
            when (val ret = musicRepository?.getUserRecentlyAdded(limit = params.requestedLoadSize)) {
                is Outcome.Success -> {
                    callback.onResult(ret.data.data, null, ret.data.nextOffset())
                    initialLoading.postValue(NetworkState.LOADED)
                    networkState.postValue(NetworkState.LOADED)
                }
                is Outcome.Failure -> {
                    initialLoading.postValue(NetworkState(NetworkState.Status.FAILED, ret.e.localizedMessage))
                    networkState.postValue(NetworkState(NetworkState.Status.FAILED, ret.e.localizedMessage))
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RecentlyAddedEntity>) {
        networkState.postValue(NetworkState.LOADING)
        uiScope.launch(Dispatchers.IO) {
            when (val ret = musicRepository?.getUserRecentlyAdded(limit = params.requestedLoadSize, offset = params.key)) {
                is Outcome.Success -> {
                    val next = ret.data.nextOffset()

                    callback.onResult(ret.data.data, next)
                    networkState.postValue(NetworkState.LOADED)

                }
                is Outcome.Failure -> networkState.postValue(NetworkState(NetworkState.Status.FAILED, ret.e.localizedMessage))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RecentlyAddedEntity>) = Unit
}