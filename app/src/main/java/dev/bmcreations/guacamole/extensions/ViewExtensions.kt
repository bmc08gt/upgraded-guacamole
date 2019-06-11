package dev.bmcreations.guacamole.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment

/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) = setPadding(
        cutout.safeInsetLeft,
        cutout.safeInsetTop,
        cutout.safeInsetRight,
        cutout.safeInsetBottom)

    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}

/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

    // Make sure that the dialog's window is in full screen
    window?.decorView?.systemUiVisibility = FLAGS_FULLSCREEN

    // Show the dialog while still in immersive mode
    show()

    // Set the dialog to focusable again
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}

// visibility
fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.isInvisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

private fun resourceHasPackage(id: Int): Boolean {
    return id.ushr(24) != 0
}

val View.identifier: String
    get() {
        val r = this.resources
        r?.let {
            if (id > 0 && resourceHasPackage(id)) {
                try {
                    val pkgname: String = when (id and -0x1000000) {
                        0x7f000000 -> "app"
                        0x01000000 -> "android"
                        else -> r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryname = r.getResourceEntryName(id)
                    return "$pkgname:$typename/$entryname"
                } catch (e: Resources.NotFoundException) { }
            }
        }
        return "unknown"
    }

fun Activity.closeIme() {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also { imm ->
        val view = this.currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}

fun Fragment.closeIme() {
    val activity = ((this as? Fragment)?.activity)
    activity?.closeIme()
}

fun Activity.openIme() {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also { imm ->
        val view = this.currentFocus ?: View(this)
        imm.showSoftInput(view, 0)
    }
}

fun Fragment.openIme() {
    val activity = ((this as? Fragment)?.activity)
    activity?.openIme()
}

fun View.tintBackground(colorResId: Int) {
    val wrapDrawable = DrawableCompat.wrap(background).mutate()
    DrawableCompat.setTint(wrapDrawable, colorResId)
    background = wrapDrawable
}

fun View.handleLayout(onLayout: (() -> Unit)? = null) {
    if (this.width > 0) {
        onLayout?.invoke()
    } else {
        this.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            var observerAdded = false
            override fun onGlobalLayout() {
                onLayout?.invoke()
                this@handleLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}

fun View.onLayoutChange(onLayout: (() -> Unit)? = null) {
    val v= this
    if (this.width > 0) {
        onLayout?.invoke()
    } else {
        this.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
            override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int,
                                        p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                onLayout?.invoke()
                v.removeOnLayoutChangeListener(this)
            }
        })
    }
}

val Activity.rootView: View
    get() {
        return findViewById<View>(android.R.id.content)
    }