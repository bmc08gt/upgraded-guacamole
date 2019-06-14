package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import dev.bmcreations.musickit.networking.extensions.sumByLong
import dev.bmcreations.musickit.networking.operator.FieldProperty
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class LibraryPlaylistResult(val data: List<LibraryPlaylist>) : Parcelable

var LibraryPlaylist.tracks by FieldProperty<LibraryPlaylist, List<PlaylistTrack>> { emptyList() }
val LibraryPlaylist.durationInMillis: Long
    get() {
        return tracks.sumByLong { it.attributes?.durationInMillis ?: 0 }
    }
val LibraryPlaylist.durationInMinutes: Long
    get() {
        return TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
    }
val LibraryPlaylist.Attributes.Artwork.urlWithDimensions: String?
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
data class LibraryPlaylist(
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("attributes")
    val attributes: Attributes?
) : Parcelable {
    @Parcelize
    data class Attributes(
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("canEdit")
        val canEdit: Boolean?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("description")
        val description: Description?
    ) : Parcelable {
        @Parcelize
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("kind")
            val kind: String?,
            @SerializedName("isLibrary")
            val isLibrary: Boolean?,
            @SerializedName("globalId")
            val globalId: String?
        ) : Parcelable

        @Parcelize
        data class Artwork(
            @SerializedName("width")
            val width: Int?,
            @SerializedName("height")
            val height: Int?,
            @SerializedName("url")
            val url: String?
        ) : Parcelable

        @Parcelize
        data class Description(
            @SerializedName("standard")
            val standard: String?
        ) : Parcelable
    }
}