package dev.bmcreations.guacamole.providers

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

@SuppressLint("Registered")
open class ContentProviderGenerator(private val gen: (context: Context) -> Unit) : ContentProvider() {

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        throw Exception("unimplemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw Exception("unimplemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw Exception("unimplemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw Exception("unimplemented")
    }

    override fun getType(uri: Uri): String {
        throw Exception("unimplemented")
    }

    override fun onCreate(): Boolean {
        gen.invoke(context!!)
        return true
    }
}
