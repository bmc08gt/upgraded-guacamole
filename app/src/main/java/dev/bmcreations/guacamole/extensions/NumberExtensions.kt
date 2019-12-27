package dev.bmcreations.guacamole.extensions

import android.content.res.Resources
import android.util.TypedValue
import java.text.NumberFormat
import kotlin.math.roundToInt

val Number.dp: Int
get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).roundToInt()

val Number.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

const val HUNDRED = 100.0
const val THOUSAND = 1000.0
const val MILLION = 1000000.0
const val BILLION = 1000000000.0

fun Number.formatted(decimals: Int = 0): String {
    val formatter = "%.${decimals}f"

    return if (decimals == 0) {
        NumberFormat.getInstance().format(this)
    } else when {
        this.toDouble() >= BILLION -> "${formatter.format(this.toDouble() / BILLION)}B"
        this.toDouble() >= MILLION -> "${formatter.format(this.toDouble() / MILLION)}M"
        this.toDouble() >= THOUSAND -> "${formatter.format(this.toDouble() / THOUSAND)}K"
        else -> this.toString()
    }
}
