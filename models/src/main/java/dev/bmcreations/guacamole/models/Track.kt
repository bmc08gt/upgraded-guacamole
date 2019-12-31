package dev.bmcreations.guacamole.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackResult(val data: List<Track>) : Parcelable

@Parcelize
data class Track(
    val attributes: Attributes?,
    val href: String?,
    val id: String?,
    val type: String?,
    val relationships: Relationships?
) : Parcelable {
    @Parcelize
    data class Attributes(
        val artwork: Artwork?,
        val artistName: String?,
        val genreNames: List<String?>?,
        val durationInMillis: Int?,
        val releaseDate: String?,
        val name: String?,
        val albumName: String?,
        val playParams: PlayParams?,
        val trackNumber: Int?,
        val contentRating: String?
    ) : Parcelable {

        @Parcelize
        data class Artwork(
            @SerializedName("height")
            val height: Int?,
            @SerializedName("url")
            val url: String?,
            @SerializedName("width")
            val width: Int?
        ) : Parcelable

        @Parcelize
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("isLibrary")
            val isLibrary: Boolean?,
            @SerializedName("kind")
            val kind: String?,
            @SerializedName("reporting")
            val reporting: Boolean?,
            @SerializedName("catalogId")
            val catalogId: String?
        ) : Parcelable
    }

    @Parcelize
    data class Relationships(
        val albums: Albums?,
        val artists: Artists?
    ) : Parcelable {
        @Parcelize
        data class Albums(
            val `data`: List<dev.bmcreations.guacamole.models.LibraryAlbum?>?,
            val href: String?
        ) : Parcelable

        @Parcelize
        data class Artists(
            val `data`: List<dev.bmcreations.guacamole.models.LibraryArtist>
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
