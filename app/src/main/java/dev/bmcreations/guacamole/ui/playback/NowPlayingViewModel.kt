package dev.bmcreations.guacamole.ui.playback

import androidx.lifecycle.ViewModel
import dev.bmcreations.guacamole.media.MediaState
import dev.bmcreations.guacamole.models.apple.Container
import dev.bmcreations.guacamole.models.apple.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.AnkoLogger

class NowPlayingViewModel private constructor(
    private val mediaState: MediaState
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ViewModel(), AnkoLogger {

    companion object Factory {
        fun create(mediaState: MediaState): NowPlayingViewModel {
            return NowPlayingViewModel( mediaState)
        }
    }

    fun playCollection(collection: Container?) {
        mediaState.play(collection)
    }

    fun play(track: TrackEntity?) {
        mediaState.play(track)
    }

    fun shuffleCollection(collection: Container?) {
        mediaState.shuffle(collection)
    }

    fun playPause() {
        mediaState.handlePlayStateChange()
    }
}
