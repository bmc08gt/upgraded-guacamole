package dev.bmcreations.musickit.networking.api.models

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import kotlinx.android.parcel.Parcelize

sealed class TrackEntity : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is TrackEntity) {
            when (this) {
                is AlbumTrackEntity -> {
                    if (other is AlbumTrackEntity) {
                        this === other
                    } else {
                        false
                    }
                }
                is PlaylistTrackEntity -> {
                    if (other is PlaylistTrackEntity) {
                        this === other
                    } else {
                        false
                    }
                }
            }
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    abstract fun toMetadata(): MediaMetadataCompat
}
@Parcelize
data class PlaylistTrackEntity(val track: PlaylistTrack): TrackEntity(), Parcelable {
    override fun toMetadata(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder().apply {
            this@PlaylistTrackEntity.track.also { track ->
                this.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.id)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.attributes?.albumName)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.attributes?.artistName)
                this.putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.attributes?.name)
                this.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.attributes?.durationInMillis ?: 0)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, track.attributes?.trackNumber?.toLong() ?: 0)
            }
        }.build()
    }
}

@Parcelize
data class AlbumTrackEntity(val track: LibraryAlbum.Relationships.Tracks.Data): TrackEntity(), Parcelable {
    override fun toMetadata(): MediaMetadataCompat {
        return MediaMetadataCompat.Builder().apply {
            this@AlbumTrackEntity.track.also { track ->
                this.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.id)
               // this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.attributes?.p)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.attributes?.artistName)
                this.putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.attributes?.name)
                this.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.attributes?.durationInMillis ?: 0)
                this.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, track.attributes?.artwork?.urlWithDimensions)
                this.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, track.attributes?.trackNumber?.toLong() ?: 0)
            }
        }.build()
    }
}

