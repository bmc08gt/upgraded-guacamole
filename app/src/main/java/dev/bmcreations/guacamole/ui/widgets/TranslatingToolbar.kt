package dev.bmcreations.guacamole.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.github.ksoichiro.android.observablescrollview.ScrollUtils
import com.google.android.material.appbar.AppBarLayout
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.handleLayout
import kotlinx.android.synthetic.main.translating_toolbar.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.lang.ref.WeakReference

class TranslatingToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Toolbar(context, attrs, defStyle), AnkoLogger {

    private var baseTranslationY = 0
    private var baseAlpha = 0f
    private var baseElevation = 8f

    private var translatingEnabled = true
    private var showElevation = false

    private var appBarReference: WeakReference<AppBarLayout?> = WeakReference(parent as? AppBarLayout)

    val view: View = View.inflate(context, R.layout.translating_toolbar, this)

    init {
        view.title.handleLayout {
            baseTranslationY = view.height
            baseAlpha = 0f
            initializeEffects()
        }
    }

    private fun initializeEffects() {
        val (y, alpha) = if (translatingEnabled) {
            Pair(-view.height.toFloat(), 0f)
        } else {
            Pair(0f, 1f)
        }

        translationY = y
        setAlpha(alpha)
    }

    override fun setTitle(resId: Int) {
        view.title.setText(resId)
        initializeEffects()
    }

    override fun setTitle(title: CharSequence?) {
        view.title.text = title
        initializeEffects()
    }

    override fun getTranslationY(): Float {
        return view.title.translationY
    }

    override fun setTranslationY(translationY: Float) {
        view.title.translationY = translationY
    }

    override fun getAlpha(): Float {
        return view.title.alpha
    }

    override fun setAlpha(alpha: Float) {
        view.title.alpha = alpha
    }

    fun showElevation(show: Boolean) {
        showElevation = show
        applyElevation()
    }

    override fun setElevation(elevation: Float) {
        baseElevation = elevation
        applyElevation()
    }

    private fun applyElevation() {
        appBarReference.get()?.let {
            ViewCompat.setElevation(it, if (showElevation) baseElevation else 0f)
        }
    }

    fun registerAppBarLayout(appBar: AppBarLayout?) {
        appBarReference = WeakReference(appBar)
    }

    fun translate(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float {
        if (dragging || scrollY < height) {
            if (firstScroll) {
                val y = translationY

                if (-height < y && height < scrollY) {
                    baseTranslationY = view.height
                }
            }
            val dy = ScrollUtils.getFloat(
                (scrollY.toFloat() - baseTranslationY),
                -height.toFloat(),
                0f
            )

            val dAlpha = dy / -height.toFloat()
            translationY = -dy
            alpha = 1f - dAlpha

            return dy
        }
        return 0f
    }

    fun enableTranslationEffect(enable: Boolean) {
        translatingEnabled = enable
        initializeEffects()
    }
}
