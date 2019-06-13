package dev.bmcreations.musickit.networking.api

import android.media.MediaMetadata

val MediaMetadata.mediaId: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)
    }

val MediaMetadata.songName: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_TITLE)
    }


val MediaMetadata.artistName: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_ARTIST)
    }

val MediaMetadata.albumName: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_ALBUM)
    }


val MediaMetadata.albumArtworkUrl: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
    }


val MediaMetadata.fullArtworkUri: String
    get() {
        return this.getString(MediaMetadata.METADATA_KEY_ART_URI)
    }


val MediaMetadata.durationMillis: Long
    get() {
        return this.getLong(MediaMetadata.METADATA_KEY_DURATION)
    }