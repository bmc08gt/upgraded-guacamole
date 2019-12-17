package dev.bmcreations.guacamole.graphs

import com.apple.android.music.playback.controller.MediaPlayerController

data class AppGraph(
    val sessionGraph: SessionGraph,
    val networkGraph: NetworkGraph
)
