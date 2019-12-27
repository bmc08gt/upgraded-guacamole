package dev.bmcreations.musickit.networking.api.models


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LibraryArtistsResult(val data: List<LibraryArtist>) : Parcelable

@Parcelize
data class LibraryArtistResult(val data: LibraryArtist) : Parcelable
@Parcelize
data class LibraryArtist(
    val attributes: Attributes?,
    val href: String?,
    val id: String?,
    val type: String?
) : Parcelable {
    @Parcelize
    data class Attributes(
        val name: String?,
        val artwork: Artwork?
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
    }
}
