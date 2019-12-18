package dev.bmcreations.guacamole.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.widget.TintTypedArray
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors
import dev.bmcreations.guacamole.extensions.sp
import kotlinx.android.synthetic.main.avocado_toolbar.view.*
import java.lang.ref.WeakReference

@SuppressLint("RestrictedApi")
class AvocadoToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var _typeface: Typeface? = null
    private var style = Typeface.NORMAL
    private var fontWeight = FONT_WEIGHT_UNSPECIFIED

    private var asyncFontPending = false

    companion object {
        const val FONT_WEIGHT_UNSPECIFIED = -1
    }

    init {
        View.inflate(context, R.layout.avocado_toolbar, this)

        attrs?.let { attributes ->
            TintTypedArray.obtainStyledAttributes(context, attributes, R.styleable.AvocadoToolbar)
                .use {
                    if (it.hasValue(R.styleable.AvocadoToolbar_android_textSize)) {
                        setTextSize(
                            it.getFloat(
                                R.styleable.AvocadoToolbar_android_textSize,
                                24.sp(context).toFloat()
                            )
                        )
                    }

                    if (it.hasValue(R.styleable.AvocadoToolbar_android_textColor)) {
                        setTextColor(
                            it.getColor(
                                R.styleable.AvocadoToolbar_android_textColor,
                                context.colors[R.color.colorAccent]
                            )
                        )
                    }

                    if (it.hasValue(R.styleable.AvocadoToolbar_android_text)) {
                        setText(it.getString(R.styleable.AvocadoToolbar_android_text))
                    }

                    if (it.hasValue(R.styleable.AvocadoToolbar_android_fontFamily)) {
                        loadTypeface(it)
                        setTypeface(_typeface)
                    }
                }
        }
    }

    fun setTextSize(size: Float) {
        title.textSize = size
    }

    fun setTextColor(@ColorInt color: Int) {
        title.setTextColor(color)
    }

    fun setText(@StringRes textResId: Int) {
        title.setText(textResId)
    }

    fun setText(text: String?) {
        title.text = text
    }

    fun setTypeface(typeface: Typeface?) {
        title.typeface = typeface
    }

    private fun loadTypeface(attrs: TintTypedArray) {
        style = attrs.getInt(R.styleable.AvocadoToolbar_android_textStyle, style)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fontWeight = attrs.getInt(
                R.styleable.AvocadoToolbar_android_textFontWeight,
                FONT_WEIGHT_UNSPECIFIED
            )
            if (fontWeight != FONT_WEIGHT_UNSPECIFIED) {
                style = Typeface.NORMAL or (style and Typeface.ITALIC)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val textViewWeak = WeakReference<TextView>(title)
            val replyCallback: ResourcesCompat.FontCallback =
                object : ResourcesCompat.FontCallback() {
                    override fun onFontRetrieved(typeface: Typeface) {
                        var tf = typeface
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (fontWeight != FONT_WEIGHT_UNSPECIFIED) {
                                tf = Typeface.create(
                                    typeface, fontWeight,
                                    style and Typeface.ITALIC != 0
                                )
                            }
                        }
                        onAsyncTypefaceReceived(textViewWeak, tf)
                    }

                    override fun onFontRetrievalFailed(reason: Int) { // Do nothing.
                    }
                }
            try { // Note the callback will be triggered on the UI thread.
                val typeface: Typeface? = attrs.getFont(
                    R.styleable.AvocadoToolbar_android_fontFamily,
                    style,
                    replyCallback
                )
                if (typeface != null) {
                    _typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                        && fontWeight != FONT_WEIGHT_UNSPECIFIED
                    ) {
                        Typeface.create(
                            Typeface.create(typeface, Typeface.NORMAL), fontWeight,
                            style and Typeface.ITALIC != 0
                        )
                    } else {
                        typeface
                    }
                }
                // If this call gave us an immediate result, ignore any pending callbacks.
                asyncFontPending = _typeface == null
            } catch (e: UnsupportedOperationException) { // Expected if it is not a font resource.
            } catch (e: NotFoundException) {
            }
        }
        if (_typeface == null) { // Try with String. This is done by TextView JB+, but fails in ICS
            val fontFamilyName = attrs.getString(R.styleable.AvocadoToolbar_android_fontFamily)
            if (fontFamilyName != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    && fontWeight != FONT_WEIGHT_UNSPECIFIED
                ) {
                    _typeface = Typeface.create(
                        Typeface.create(fontFamilyName, Typeface.NORMAL), fontWeight,
                        style and Typeface.ITALIC != 0
                    )
                } else {
                    _typeface = Typeface.create(fontFamilyName, style)
                }
            }
        }
    }

    internal fun onAsyncTypefaceReceived(
        textViewWeak: WeakReference<TextView>,
        typeface: Typeface
    ) {
        if (asyncFontPending) {
            _typeface = typeface
            val textView = textViewWeak.get()
            textView?.setTypeface(typeface, style)
        }
    }

    /**
     * Executes the given [block] function on this TypedArray and then recycles it.
     *
     * @see kotlin.io.use
     */
    @SuppressLint("RestrictedApi")
    inline fun <R> TintTypedArray.use(block: (TintTypedArray) -> R): R {
        return block(this).also {
            recycle()
        }
    }
}
