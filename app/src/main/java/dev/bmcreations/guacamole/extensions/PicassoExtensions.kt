package dev.bmcreations.guacamole.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

fun picasso(cb: ((Picasso) -> Unit)) = cb.invoke(Picasso.get())

open class SimpleTarget: Target {
    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = Unit
    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) = Unit
}