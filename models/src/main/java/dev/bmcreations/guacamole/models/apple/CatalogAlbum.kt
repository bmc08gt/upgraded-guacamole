package dev.bmcreations.guacamole.models.apple


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatalogAlbumResult(val data: List<CatalogAlbum>): Parcelable

@Parcelize
data class CatalogAlbum(
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
): Parcelable {
    @Parcelize
    data class Attributes(
        @SerializedName("artistName")
        val artistName: String?,
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("copyright")
        val copyright: String?,
        @SerializedName("editorialNotes")
        val editorialNotes: EditorialNotes?,
        @SerializedName("genreNames")
        val genreNames: List<String?>?,
        @SerializedName("isComplete")
        val isComplete: Boolean?,
        @SerializedName("isMasteredForItunes")
        val isMasteredForItunes: Boolean?,
        @SerializedName("isSingle")
        val isSingle: Boolean?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("recordLabel")
        val recordLabel: String?,
        @SerializedName("releaseDate")
        val releaseDate: String?,
        @SerializedName("trackCount")
        val trackCount: Int?,
        @SerializedName("url")
        val url: String?
    ): Parcelable {
        @Parcelize
        data class Artwork(
            @SerializedName("bgColor")
            val bgColor: String?,
            @SerializedName("height")
            val height: Int?,
            @SerializedName("textColor1")
            val textColor1: String?,
            @SerializedName("textColor2")
            val textColor2: String?,
            @SerializedName("textColor3")
            val textColor3: String?,
            @SerializedName("textColor4")
            val textColor4: String?,
            @SerializedName("url")
            val url: String?,
            @SerializedName("width")
            val width: Int?
        ): Parcelable

        @Parcelize
        data class EditorialNotes(
            @SerializedName("short")
            val short: String?,
            @SerializedName("standard")
            val standard: String?
        ): Parcelable

        @Parcelize
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("kind")
            val kind: String?
        ): Parcelable
    }

    @Parcelize
    data class Relationships(
        val artists: Artists?,
        val tracks: Tracks?
    ) : Parcelable {
        @Parcelize
        data class Artists(
            val `data`: List<Data?>?,
            val href: String?
        ) : Parcelable {
            @Parcelize
            data class Data(
                val href: String?,
                val id: String?,
                val type: String?
            ) : Parcelable
        }

        @Parcelize
        data class Tracks(
            val `data`: List<Data?>?,
            val href: String?
        ) : Parcelable {
            @Parcelize
            data class Data(
                val attributes: Attributes?,
                val href: String?,
                val id: String?,
                val type: String?
            ) : Parcelable {
                @Parcelize
                data class Attributes(
                    val artistName: String?,
                    val artwork: Artwork?,
                    val composerName: String?,
                    val discNumber: Int?,
                    val durationInMillis: Int?,
                    val genreNames: List<String?>?,
                    val isrc: String?,
                    val name: String?,
                    val playParams: PlayParams?,
                    val previews: List<Preview?>?,
                    val releaseDate: String?,
                    val trackNumber: Int?,
                    val url: String?
                ) : Parcelable {
                    @Parcelize
                    data class Artwork(
                        val bgColor: String?,
                        val height: Int?,
                        val textColor1: String?,
                        val textColor2: String?,
                        val textColor3: String?,
                        val textColor4: String?,
                        val url: String?,
                        val width: Int?
                    ) : Parcelable

                    @Parcelize
                    data class PlayParams(
                        val id: String?,
                        val kind: String?
                    ) : Parcelable

                    @Parcelize
                    data class Preview(
                        val url: String?
                    ) : Parcelable
                }
            }
        }
    }
}
