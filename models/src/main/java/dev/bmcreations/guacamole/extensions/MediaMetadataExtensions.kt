package dev.bmcreations.guacamole.extensions

import android.support.v4.media.MediaMetadataCompat

val MediaMetadataCompat.mediaId: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    }

val MediaMetadataCompat.songName: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
    }

val MediaMetadataCompat.trackNumber: Long
    get() {
        return this.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)
    }

val MediaMetadataCompat.numOfTracks: Long
    get() {
        return this.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
    }


val MediaMetadataCompat.artistName: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    }

val MediaMetadataCompat.albumArtistName: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
    }

val MediaMetadataCompat.albumName: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
    }

val MediaMetadataCompat.albumArtworkUrl: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
    }


val MediaMetadataCompat.fullArtworkUri: String?
    get() {
        return this.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)
    }


val MediaMetadataCompat.durationMillis: Long
    get() {
        return this.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    }
