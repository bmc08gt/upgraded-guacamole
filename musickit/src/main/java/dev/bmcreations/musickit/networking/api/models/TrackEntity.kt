package dev.bmcreations.musickit.networking.api.models

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import kotlinx.android.parcel.Parcelize


@Parcelize
open class Container(
    var name: String? = null,
    var artist: String? = null,
    var artwork: String? = null,
    var trackList: List<Track>? = emptyList(),
    var isPlaylist: Boolean = false
): Parcelable

@Parcelize
data class TrackEntity(val track: Track, val container: Container): Parcelable {
    fun toMetadataBuilder(): MediaMetadataCompat.Builder {
        return MediaMetadataCompat.Builder().apply {
            this@TrackEntity.track.also { track ->
                this.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.attributes?.playParams?.catalogId)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, container.name)
                this.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.attributes?.artistName)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, container.artist)
                this.putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.attributes?.name)
                this.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.attributes?.durationInMillis ?: 0)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, track.attributes?.trackNumber?.toLong() ?: 0)
                this.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, (container.trackList?.count() ?: 0).toLong())
            }
        }
    }

    fun toMetadata(): MediaMetadataCompat {
        return toMetadataBuilder().build()
    }
}
