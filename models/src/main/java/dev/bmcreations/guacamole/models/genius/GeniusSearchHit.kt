package dev.bmcreations.guacamole.models.genius


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeniusSearchResult(
    val meta: Meta,
    val response: GeniusSearchQueryResponse
) : Parcelable

@Parcelize
data class GeniusSearchQueryResponse(
    val hits: List<GeniusSearchHit> = emptyList()
) : Parcelable


@Parcelize
data class GeniusSearchHit(
    val index: String?,
    val type: String?,
    val result: Result?
) : Parcelable {
    @Parcelize
    data class Result(
        val annotation_count: Int?,
        val api_path: String?,
        val full_title: String?,
        val header_image_thumbnail_url: String?,
        val header_image_url: String?,
        val id: Int?,
        val lyrics_owner_id: Int?,
        val lyrics_state: String?,
        val path: String?,
        val pyongs_count: Int?,
        val song_art_image_thumbnail_url: String?,
        val song_art_image_url: String?,
        val title: String?,
        val title_with_featured: String?,
        val url: String?,
        val primary_artist: PrimaryArtist?
    ) : Parcelable {
        @Parcelize
        data class PrimaryArtist(
            val api_path: String?,
            val header_image_url: String?,
            val id: Int?,
            val image_url: String?,
            val is_meme_verified: Boolean?,
            val is_verified: Boolean?,
            val name: String?,
            val url: String?,
            val iq: Int?
        ) : Parcelable
    }
}
