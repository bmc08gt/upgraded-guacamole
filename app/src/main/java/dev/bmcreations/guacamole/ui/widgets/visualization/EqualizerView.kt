package dev.bmcreations.guacamole.ui.widgets.visualization

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import dev.bmcreations.guacamole.extensions.isVisible

@SuppressLint("Recycle")
class EqualizerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {

    var visualization: Visualization? = SampledVisualization(context, attrs)

    override fun getBaseline(): Int {
        return (this.height.toFloat() - this.paddingBottom).toInt()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (this.isVisible()) {
            updateBars()
        }
    }

    private fun updateBars() {
        removeAllViews()
        visualization?.let { addView(it) }
        visualization?.updateBars { bar, pos -> addView(bar, pos) }
        visualization?.setConstraints(this)
    }

    fun animateBars() {
        visualization?.visualize()
    }

    fun stop() {
        visualization?.stopVisualization()
    }
}