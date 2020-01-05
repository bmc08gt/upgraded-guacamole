package dev.bmcreations.guacamole.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.bmcreations.guacamole.models.apple.*
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun fromRecentsToString(value: List<RecentlyAddedEntity>): String = value.toJson()
    @TypeConverter
    fun fromStringToRecents(value: String): List<RecentlyAddedEntity> = value.toList()

    @TypeConverter
    fun fromPlaylistsToString(value: List<LibraryPlaylist>): String = value.toJson()
    @TypeConverter
    fun fromStringToPlaylists(value: String): List<LibraryPlaylist> = value.toList()

    @TypeConverter
    fun fromSongsToString(value: List<Track>): String = value.toJson()
    @TypeConverter
    fun fromStringToSongs(value: String): List<Track> = value.toList()

    @TypeConverter
    fun fromAlbumsToString(value: List<Track.Relationships.Albums>): String = value.toJson()
    @TypeConverter
    fun fromStringToAlbums(value: String): List<Track.Relationships.Albums> = value.toList()

    @TypeConverter
    fun fromArtistsToString(value: List<Track.Relationships.Artists>): String = value.toJson()
    @TypeConverter
    fun fromStringToArtists(value: String): List<Track.Relationships.Artists> = value.toList()

    @TypeConverter
    fun fromStringListToString(value: List<String>): String = value.toJson()
    @TypeConverter
    fun fromStringToStringList(value: String): List<String> = value.toList()

    @TypeConverter
    fun fromLibraryAlbumsToString(value: List<LibraryAlbum>): String = value.toJson()
    @TypeConverter
    fun fromStringToLibraryAlbums(value: String): List<LibraryAlbum> = value.toList()

    @TypeConverter
    fun fromLibraryArtistsToString(value: List<LibraryArtist>): String = value.toJson()
    @TypeConverter
    fun fromStringToLibraryArtists(value: String): List<LibraryArtist> = value.toList()
}

fun <T> T.toJson(): String = gson().toJson(this, object : TypeToken<T>() {}.type)
fun <T> String.toInstance(): T = getInstanceFromJson(this, object : TypeToken<T>() {}.type)
fun <T> List<T>.toJson(): String = gson().toJson(this, object : TypeToken<List<T>>() {}.type)
fun <T> String.toList(): List<T> = getInstanceFromJson(this, object : TypeToken<List<T>>() {}.type)


/**
 * added - <code> GsonBuilder.serializeSpecialFloatingPointValues() </code> -
 * to avoid "infinity is not a valid double value as per JSON specification exception"
 */
private fun gson(): Gson {
    return GsonBuilder().serializeSpecialFloatingPointValues().create()
}

private inline fun <reified T> toJson(obj: T, typeToken: Type): String {
    return gson().toJson(obj, typeToken)
}

private fun <T> getInstanceFromJson(json: String?, typeToken: Type): T {
    return gson().fromJson<T>(json, typeToken)
}
