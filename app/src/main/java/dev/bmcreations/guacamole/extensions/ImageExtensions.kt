package dev.bmcreations.guacamole.extensions

import android.graphics.Bitmap
import android.media.Image


fun Image.toBitmap(): Bitmap? {
    val planes = this.planes
    val buffer = planes[0].buffer ?: return null

    var pixelStride = planes[0].pixelStride
    if (pixelStride <= 0) {
        pixelStride = 1
    }
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * this.width
    // create bitmap
    val bitmap = Bitmap.createBitmap(this.width + rowPadding / pixelStride, this.height, Bitmap.Config.ARGB_8888)
   // bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}