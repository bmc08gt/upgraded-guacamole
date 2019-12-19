package dev.bmcreations.guacamole.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.github.ksoichiro.android.observablescrollview.ScrollUtils
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.handleLayout
import kotlinx.android.synthetic.main.translating_toolbar.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class TranslatingToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Toolbar(context, attrs, defStyle), AnkoLogger {

    private var baseTranslationY = 0
    private var baseAlpha = 0f

    val view: View = View.inflate(context, R.layout.translating_toolbar, this)

    init {
        view.title.handleLayout {
            baseTranslationY = view.height
            baseAlpha = 0f
            view.title.animate().translationY(-view.height.toFloat()).alpha(0f).start()
        }
    }

    override fun setTitle(resId: Int) {
        view.title.setText(resId)
        view.title.animate().translationY(-view.height.toFloat()).alpha(0f).start()
    }

    override fun setTitle(title: CharSequence?) {
        view.title.text = title
        view.title.animate().translationY(-view.height.toFloat()).alpha(0f).start()
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
}
