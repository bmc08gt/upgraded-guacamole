package dev.bmcreations.guacamole.models.apple


import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import dev.bmcreations.guacamole.converters.Converters
import dev.bmcreations.guacamole.models.PagedListImpl
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@Parcelize
open class RecentlyAddedResult: PagedListImpl<RecentlyAddedEntity>(), Parcelable

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

@Entity
@TypeConverters(Converters::class)
@Parcelize
data class RecentlyAddedEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    @Embedded(prefix = "attr_")
    val attributes: Attributes?,
    val href: String?,
    val id: String,
    val type: String?
) : Parcelable {
    @Parcelize
    data class Attributes(
        val artistName: String?,
        @Embedded(prefix = "art_")
        val artwork: Artwork?,
        val name: String?,
        @Embedded(prefix = "play_")
        val playParams: PlayParams?,
        val trackCount: Int?
    ) : Parcelable {
        @Parcelize
        data class Artwork(
            val height: Int?,
            val url: String?,
            val width: Int?
        ) : Parcelable

        @Parcelize
        data class PlayParams(
            val id: String?,
            val isLibrary: Boolean?,
            val kind: String?
        ) : Parcelable
    }
}
