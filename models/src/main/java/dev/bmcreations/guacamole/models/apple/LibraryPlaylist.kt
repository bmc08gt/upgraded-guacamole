package dev.bmcreations.guacamole.models.apple


import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import dev.bmcreations.guacamole.converters.Converters
import dev.bmcreations.guacamole.operator.NullableFieldProperty
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class LibraryPlaylistResult(val data: List<LibraryPlaylist>) : Parcelable

fun LibraryPlaylist.toEntities(): List<TrackEntity> = trackList?.map { t ->
    TrackEntity(
        t,
        this
    )
} ?: emptyList()

val LibraryPlaylist.durationInMillis: Long
    get() {
        return trackList?.sumByLong { it.attributes?.durationInMillis?.toLong() ?: 0 } ?: 0
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

var LibraryPlaylist.Attributes.curator by NullableFieldProperty<LibraryPlaylist.Attributes, String>()
var LibraryPlaylist.Attributes.trackCount by NullableFieldProperty<LibraryPlaylist.Attributes, Int>()

@Entity
@Parcelize
@TypeConverters(Converters::class)
data class LibraryPlaylist(
    @PrimaryKey
    val id: String,
    val type: String?,
    val href: String?,
    @Embedded(prefix = "attrs_")
    val attributes: Attributes?
) : Container(),  Parcelable {
    @Parcelize
    data class Attributes(
        @Embedded(prefix = "art_")
        val artwork: Artwork?,
        @Embedded(prefix = "play_")
        val playParams: PlayParams?,
        val canEdit: Boolean?,
        @SerializedName("name")
        val name: String?,
        @Embedded(prefix = "desc_")
        val description: Description?
    ) : Parcelable {
        @Parcelize
        data class PlayParams(
            val id: String?,
            val kind: String?,
            val isLibrary: Boolean?,
            val globalId: String?
        ) : Parcelable

        @Parcelize
        data class Artwork(
            val width: Int?,
            val height: Int?,
            val url: String?
        ) : Parcelable

        @Parcelize
        data class Description(
            val standard: String?
        ) : Parcelable
    }
}
