package dev.bmcreations.musickit.networking.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentlyAddedResult(val data: List<RecentlyAddedEntity>) : Parcelable

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
        ) : Parcelable {
            val urlWithDimensions: String?
                get() {
                    return url?.let { url ->
                        var populated = url
                        width?.let { w ->
                            populated = populated.replace("{w}", w.toString(), ignoreCase = false)
                            height?.let { h ->
                                populated = populated.replace("{h}", h.toString(), ignoreCase = false)
                            }
                        }
                        return populated
                    }

                }
        }

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