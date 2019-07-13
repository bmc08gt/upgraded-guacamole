package dev.bmcreations.guacamole.ui.widgets.visualization

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors
import dev.bmcreations.guacamole.extensions.handleLayout

abstract class Visualization(context: Context, val attrs: AttributeSet? = null): View(context, attrs) {

    companion object {
        const val BAR_COUNT = 3
    }

    val bars = mutableListOf<View>()

    var color: Int = context.colors[R.color.colorAccent]
    var duration: Long = 3000L

    var animating = false

    init {
        attrs?.let { attributes ->
            context.obtainStyledAttributes(attributes, R.styleable.EqualizerView).use {
                if (it.hasValue(R.styleable.EqualizerView_color)) {
                    color = it.getColor(R.styleable.EqualizerView_color, android.R.attr.colorAccent)
                }

                if (it.hasValue(R.styleable.EqualizerView_animationDuration)) {
                    duration = it.getInteger(R.styleable.EqualizerView_animationDuration, 3000).toLong()
                }
            }
        }
    }

    fun setupBars() {
        bars.clear()
        for (i in 0 until BAR_COUNT) {
            val b = View(context, attrs)
            bars.add(b)
        }
    }

    fun updateBars(onBar: (View, Int) -> Unit) {
        bars.forEachIndexed { index, bar ->
            bar.id = generateViewId()
            bar.setBackgroundColor(color)
            bar.handleLayout {
                bar.pivotY = this.height.toFloat() - this.paddingBottom - this.paddingTop
            }
            onBar.invoke(bar, index)
        }
    }

    open fun visualize() {
        animating = true
    }

    open fun stopVisualization() {
        animating = false
    }
}