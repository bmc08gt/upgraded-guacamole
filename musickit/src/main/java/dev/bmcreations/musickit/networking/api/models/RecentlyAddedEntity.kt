package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@Parcelize
data class RecentlyAddedResult(private val next: String? = null, val data: List<RecentlyAddedEntity>) : Parcelable {

    fun nextOffset(): Int? {
        return next?.let { "http://foobar.com$it".toHttpUrlOrNull()?.queryParameter("offset")?.toInt() }
    }
}

val RecentlyAddedEntity.Attributes.Artwork.urlWithDimensions: String?
    get() {
        return url?.let { url ->
            val w = width ?: 600
            val h = height ?: 600
            return url.replace(
                "{w}", w.toString(), ignoreCase = false).replace(
                "{h}", h.toString(), ignoreCase = false)
        }
    }

@Parcelize
data class RecentlyAddedEntity(
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
        @SerializedName("name")
        val name: String?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("trackCount")
        val trackCount: Int?
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
            val kind: String?
        ) : Parcelable
    }
}
