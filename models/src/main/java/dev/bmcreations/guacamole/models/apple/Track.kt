package dev.bmcreations.guacamole.models.apple

import android.os.Parcelable
import androidx.room.*
import dev.bmcreations.guacamole.converters.Converters
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackResult(val data: List<Track>) : Parcelable

@Entity
@Parcelize
@TypeConverters(Converters::class)
data class Track(
    @Embedded(prefix = "attr_")
    val attributes: Attributes?,
    val href: String?,
    @PrimaryKey
    val id: String,
    val type: String?,
    @Embedded(prefix = "relations_")
    val relationships: Relationships?
) : Parcelable {
    @Parcelize
    data class Attributes(
        @Embedded(prefix = "art_")
        val artwork: Artwork?,
        val artistName: String?,
        @TypeConverters(Converters::class)
        val genreNames: List<String>?,
        val durationInMillis: Int?,
        val releaseDate: String?,
        val name: String?,
        val albumName: String?,
        @Embedded(prefix = "play_")
        val playParams: PlayParams?,
        val trackNumber: Int?,
        val contentRating: String?
    ) : Parcelable {

        @Parcelize
        data class Artwork(
            val height: Int?,
            val url: String?,
            val width: Int?
        ) : Parcelable

        @Parcelize
        data class PlayParams(
            val id: String?,
            val isLibrary: Boolean?,
            val kind: String?,
            val reporting: Boolean?,
            val catalogId: String?
        ) : Parcelable
    }

    @Parcelize
    data class Relationships(
        @Embedded(prefix = "albums_")
        @TypeConverters(Converters::class)
        val albums: Albums?,
        @Embedded(prefix = "artists_")
        @TypeConverters(Converters::class)
        val artists: Artists?
    ) : Parcelable {
        @Parcelize
        data class Albums(
            @TypeConverters(Converters::class)
            val `data`: List<LibraryAlbum>?,
            val href: String?
        ) : Parcelable

        @Parcelize
        data class Artists(
            @TypeConverters(Converters::class)
            val `data`: List<LibraryArtist>
        ): Parcelable
    }
}

val Track.Attributes.Artwork.urlWithDimensions: String?
    get() {
        return url?.let { url ->
            val w = width ?: 600
            val h = height ?: 600
            return url.replace(
                "{w}", w.toString(), ignoreCase = false
            ).replace(
                "{h}", h.toString(), ignoreCase = false
            )
        }
    }

val Track.Attributes.isExplicit: Boolean
    get() {
        return contentRating?.equals("explicit", ignoreCase = true) ?: false
    }
