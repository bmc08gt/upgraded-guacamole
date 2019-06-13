package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize data class PlaylistResult(val data: List<Playlist>): Parcelable

@Parcelize
data class Playlist(
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("attributes")
    val attributes: Attributes?
): Parcelable {
    @Parcelize
    data class Attributes(
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("canEdit")
        val canEdit: Boolean?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("artwork")
        val artwork: Artwork?
    ): Parcelable {
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
            val isLibrary: Boolean?
        ): Parcelable
    }
}