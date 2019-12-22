package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import dev.bmcreations.musickit.extensions.sumByLong
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class LibraryAlbumResult(val data: List<LibraryAlbum>) : Parcelable

fun LibraryAlbum.toEntities(): List<TrackEntity> = relationships?.tracks?.data?.filterNotNull()?.map { t -> TrackEntity(t, this) } ?: emptyList()

val LibraryAlbum.Relationships.Tracks.durationInMillis: Long
    get() {
        return this.data?.sumByLong { it?.attributes?.durationInMillis ?: 0L } ?: 0
    }
val LibraryAlbum.Relationships.Tracks.durationInMinutes: Long
    get() {
        return TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
    }

val LibraryAlbum.Attributes.Artwork.urlWithDimensions: String?
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

@Parcelize
data class LibraryAlbum(
    @SerializedName("attributes")
    val attributes: Attributes?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("relationships")
    val relationships: Relationships?,
    @SerializedName("type")
    val type: String?
) : Container(attributes?.name, attributes?.artistName, relationships?.tracks?.data?.filterNotNull() ?: emptyList()), Parcelable {
    @Parcelize
    data class Relationships(
        @SerializedName("tracks")
        val tracks: Tracks?
    ) : Parcelable {
        @Parcelize
        data class Tracks(
            @SerializedName("data")
            val `data`: List<Track?>?,
            @SerializedName("href")
            val href: String?
        ) : Parcelable
    }

    @Parcelize
    data class Attributes(
        @SerializedName("artistName")
        val artistName: String?,
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("trackCount")
        val trackCount: Int?
    ) : Parcelable {
        @Parcelize
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("isLibrary")
            val isLibrary: Boolean?,
            @SerializedName("kind")
            val kind: String?
        ) : Parcelable

        @Parcelize
        data class Artwork(
            @SerializedName("height")
            val height: Int?,
            @SerializedName("url")
            val url: String?,
            @SerializedName("width")
            val width: Int?
        ) : Parcelable
    }
}
