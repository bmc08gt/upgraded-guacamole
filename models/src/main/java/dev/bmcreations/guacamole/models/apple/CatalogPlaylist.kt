package dev.bmcreations.guacamole.models.apple


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatalogPlaylistResult(val data: List<CatalogPlaylist>): Parcelable

@Parcelize
data class CatalogPlaylist(
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("attributes")
    val attributes: Attributes?,
    @SerializedName("relationships")
    val relationships: Relationships?
): Parcelable {
    @Parcelize
    data class Relationships(
        @SerializedName("tracks")
        val tracks: Tracks?,
        @SerializedName("curator")
        val curator: Curator?
    ): Parcelable {
        @Parcelize
        data class Curator(
            @SerializedName("data")
            val `data`: List<Data?>?,
            @SerializedName("href")
            val href: String?
        ): Parcelable {
            @Parcelize
            data class Data(
                @SerializedName("id")
                val id: String?,
                @SerializedName("type")
                val type: String?,
                @SerializedName("href")
                val href: String?
            ): Parcelable
        }

        @Parcelize
        data class Tracks(
            @SerializedName("data")
            val `data`: List<Data?>?,
            @SerializedName("href")
            val href: String?
        ): Parcelable {
            @Parcelize
            data class Data(
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
                    @SerializedName("previews")
                    val previews: List<Preview?>?,
                    @SerializedName("artwork")
                    val artwork: Artwork?,
                    @SerializedName("artistName")
                    val artistName: String?,
                    @SerializedName("url")
                    val url: String?,
                    @SerializedName("discNumber")
                    val discNumber: Int?,
                    @SerializedName("genreNames")
                    val genreNames: List<String?>?,
                    @SerializedName("durationInMillis")
                    val durationInMillis: Int?,
                    @SerializedName("releaseDate")
                    val releaseDate: String?,
                    @SerializedName("name")
                    val name: String?,
                    @SerializedName("isrc")
                    val isrc: String?,
                    @SerializedName("albumName")
                    val albumName: String?,
                    @SerializedName("playParams")
                    val playParams: PlayParams?,
                    @SerializedName("trackNumber")
                    val trackNumber: Int?,
                    @SerializedName("composerName")
                    val composerName: String?
                ): Parcelable {
                    @Parcelize
                    data class Preview(
                        @SerializedName("url")
                        val url: String?
                    ): Parcelable

                    @Parcelize
                    data class PlayParams(
                        @SerializedName("id")
                        val id: String?,
                        @SerializedName("kind")
                        val kind: String?
                    ): Parcelable

                    @Parcelize
                    data class Artwork(
                        @SerializedName("width")
                        val width: Int?,
                        @SerializedName("height")
                        val height: Int?,
                        @SerializedName("url")
                        val url: String?,
                        @SerializedName("bgColor")
                        val bgColor: String?,
                        @SerializedName("textColor1")
                        val textColor1: String?,
                        @SerializedName("textColor2")
                        val textColor2: String?,
                        @SerializedName("textColor3")
                        val textColor3: String?,
                        @SerializedName("textColor4")
                        val textColor4: String?
                    ): Parcelable
                }
            }
        }
    }

    @Parcelize
    data class Attributes(
        @SerializedName("playlistType")
        val playlistType: String?,
        @SerializedName("url")
        val url: String?,
        @SerializedName("artwork")
        val artwork: Artwork?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("description")
        val description: Description?,
        @SerializedName("playParams")
        val playParams: PlayParams?,
        @SerializedName("curatorName")
        val curatorName: String?,
        @SerializedName("lastModifiedDate")
        val lastModifiedDate: String?
    ): Parcelable {
        @Parcelize
        data class Artwork(
            @SerializedName("width")
            val width: Int?,
            @SerializedName("height")
            val height: Int?,
            @SerializedName("url")
            val url: String?,
            @SerializedName("bgColor")
            val bgColor: String?,
            @SerializedName("textColor1")
            val textColor1: String?,
            @SerializedName("textColor2")
            val textColor2: String?,
            @SerializedName("textColor3")
            val textColor3: String?,
            @SerializedName("textColor4")
            val textColor4: String?
        ): Parcelable

        @Parcelize
        data class PlayParams(
            @SerializedName("id")
            val id: String?,
            @SerializedName("kind")
            val kind: String?
        ): Parcelable

        @Parcelize
        data class Description(
            @SerializedName("standard")
            val standard: String?,
            @SerializedName("short")
            val short: String?
        ): Parcelable
    }
}
