package dev.bmcreations.guacamole.models.apple


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import dev.bmcreations.guacamole.models.PagedListImpl

@Parcelize
open class LibrarySongResult: PagedListImpl<Track>(), Parcelable

@Parcelize
data class LibrarySong(
    val id: String?,
    val type: String?,
    val href: String?,
    val attributes: Attributes?,
    val relationships: Relationships?
) : Parcelable {
    @Parcelize
    data class Attributes(
        val artwork: Artwork?,
        val artistName: String?,
        val genreNames: List<String?>?,
        val durationInMillis: Int?,
        val releaseDate: String?,
        val name: String?,
        val albumName: String?,
        val playParams: PlayParams?,
        val trackNumber: Int?,
        val contentRating: String?
    ) : Parcelable {
        @Parcelize
        data class Artwork(
            val width: Int?,
            val height: Int?,
            val url: String?
        ) : Parcelable

        @Parcelize
        data class PlayParams(
            val id: String?,
            val kind: String?,
            val isLibrary: Boolean?,
            val reporting: Boolean?,
            val catalogId: String?
        ) : Parcelable
    }

    @Parcelize
    data class Relationships(
        val albums: Albums?,
        val artists: Artists?
    ) : Parcelable {
        @Parcelize
        data class Albums(
            val `data`: List<LibraryAlbum?>?,
            val href: String?
        ) : Parcelable

        @Parcelize
        data class Artists(
            val `data`: List<LibraryArtist>
        ): Parcelable
    }
}
