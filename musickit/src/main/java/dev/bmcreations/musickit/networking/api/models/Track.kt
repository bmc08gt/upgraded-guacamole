package dev.bmcreations.musickit.networking.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackResult(val data: List<Track>) : Parcelable

@Parcelize
data class Track(
    @SerializedName("attributes")
    val attributes: Attributes?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?
) : Parcelable {
    @Parcelize
    data class Attributes(
        @SerializedName("artistName")
        val artistName: String?,
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("contentRating")
        val contentRating: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("trackNumber")
        val trackNumber: Int?,
        @SerializedName("durationInMillis")
        val durationInMillis: Long
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
