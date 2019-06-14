package dev.bmcreations.musickit.networking.api.models

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import dev.bmcreations.musickit.networking.extensions.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: String, val name: String,
    val trackNumber: Long,
    val artistName: String, val albumName: String,
    val artworkUrl: String, val fullArtworkUrl: String,
    val durationInMillis: Long): Parcelable {

    fun toMetadata(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder().apply {
            this.putString(METADATA_KEY_MEDIA_ID, id)
            this.putString(METADATA_KEY_ALBUM, albumName)
            this.putString(METADATA_KEY_ALBUM_ARTIST, artistName)
            this.putString(METADATA_KEY_TITLE, name)
            this.putLong(METADATA_KEY_DURATION, durationInMillis)
            this.putString(METADATA_KEY_ALBUM_ART_URI, artworkUrl)
            this.putString(METADATA_KEY_ART_URI, fullArtworkUrl)
            this.putLong(METADATA_KEY_TRACK_NUMBER, trackNumber)
        }.build()
    }

    companion object Factory {
        fun fromMetadata(metadata: MediaMetadataCompat): Song {
            return Song(
                metadata.mediaId,
                metadata.songName,
                metadata.trackNumber,
                metadata.artistName,
                metadata.albumName,
                metadata.albumArtworkUrl,
                metadata.fullArtworkUri,
                metadata.durationMillis
            )
        }
    }
}