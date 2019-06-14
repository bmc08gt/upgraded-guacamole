package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistTrackResult(val data: List<PlaylistTrack>) : Parcelable

val PlaylistTrack.Attributes.Artwork.urlWithDimensions: String?
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

val PlaylistTrack.Attributes.isExplicit: Boolean
    get() {
        return contentRating?.equals("explicit", ignoreCase = true) ?: false
    }

@Parcelize
data class PlaylistTrack(
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
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("contentRating")
        val contentRating: String?,
        @SerializedName("albumName")
        val albumName: String?,
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("durationInMillis")
        val durationInMillis: Long?,
        @SerializedName("trackNumber")
        val trackNumber: Int?,
        @SerializedName("artistName")
        val artistName: String?
    ) : Parcelable {

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
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("kind")
            val kind: String?,
            @SerializedName("isLibrary")
            val isLibrary: Boolean?,
            @SerializedName("reporting")
            val reporting: Boolean?,
            @SerializedName("catalogId")
            val catalogId: String?
        ) : Parcelable
    }
}