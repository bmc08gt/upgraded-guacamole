package dev.bmcreations.guacamole.models.apple


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserStoreFrontResult(val data: List<UserStoreFront>): Parcelable

@Parcelize
data class UserStoreFront(
    @SerializedName("attributes")
    val attributes: Attributes?,
    @SerializedName("href")
    val href: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("type")
    val type: String?
): Parcelable {
    @Parcelize
    data class Attributes(
        @SerializedName("defaultLanguageTag")
        val defaultLanguageTag: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("supportedLanguageTags")
        val supportedLanguageTags: List<String?>?
    ): Parcelable
}
