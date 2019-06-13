package dev.bmcreations.guacamole.ui.library

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.auth.TokenProvider
import dev.bmcreations.guacamole.extensions.uiScope
import dev.bmcreations.musickit.networking.Outcome
import dev.bmcreations.musickit.networking.api.models.RecentlyAddedEntity
import dev.bmcreations.musickit.networking.api.music.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class LibraryViewModel(context: Context): ViewModel(), AnkoLogger {

    val musicRepo by lazy {
        MusicRepository(context, TokenProvider.with(context).devToken, TokenProvider.with(context).userToken)
    }

    val recentlyAdded: MutableLiveData<List<RecentlyAddedEntity>> = MutableLiveData()

    init {
        recentlyAdded.value = emptyList()
    }

    fun refresh() {
        updateRecentlyAdded()
    }

    fun updateRecentlyAdded() {
        musicRepo?.let { repo ->
            uiScope.launch(Dispatchers.IO) {
                val outcome = repo.getUserRecentlyAdded()
                when (outcome) {
                    is Outcome.Success -> {
                        info { "recently added items: ${outcome.data.size}" }
                        uiScope.launch { recentlyAdded.value = outcome.data }
                    }
                    is Outcome.Failure -> {
                        info { outcome.e.localizedMessage }
                    }
                }
            }
        }
    }
}