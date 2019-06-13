package dev.bmcreations.guacamole.extensions

import android.content.Context
import android.util.TypedValue

fun Number.dp(context: Context): Int = context.resources.displayMetrics.run {
    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this@dp.toFloat(), this))
}

fun Number.sp(context: Context): Int = context.resources.displayMetrics.run {
    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this@sp.toFloat(), this))
}