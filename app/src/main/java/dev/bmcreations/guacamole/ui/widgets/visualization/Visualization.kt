package dev.bmcreations.guacamole.ui.widgets.visualization

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors

abstract class Visualization(context: Context, val attrs: AttributeSet? = null): View(context, attrs) {

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

    abstract fun setup()
    abstract fun updateBars(onBar: (View, Int) -> Unit)

    open fun setConstraints(parent: ConstraintLayout) {
        // stub
    }

    open fun visualize() {
        animating = true
    }

    open fun stopVisualization() {
        animating = false
    }
}