package dev.bmcreations.guacamole.ui.widgets.visualization

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.isVisible

@SuppressLint("Recycle")
class EqualizerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {

    var group = Group(context, attrs)

    var visualization: Visualization? = SampledVisualization(context, attrs)

    init {
        ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
            group.layoutParams = it
        }

        updateBars()
    }

    override fun getBaseline(): Int {
        return (this.height.toFloat() - this.paddingBottom).toInt()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (this.isVisible()) {
            removeAllViews()
            updateBars()
        }
    }

    private fun updateBars() {
        removeAllViews()
        visualization?.let { addView(it) }

        visualization?.updateBars { bar, pos ->
            addView(bar, pos)
            setConstraints(pos, bar)
        }

        group.referencedIds = visualization?.bars?.map { it.id }?.toIntArray()

        if (group.referencedIds.size >= 2) {
            val constraints = ConstraintSet()
            constraints.clone(this)
            constraints.createHorizontalChain(
                ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                group.referencedIds, null, ConstraintSet.CHAIN_SPREAD_INSIDE
            )
            constraints.applyTo(this)
        }
    }

    private fun setConstraints(pos: Int, v: View) {
        val constraints = ConstraintSet()
        constraints.clone(this)
        constraints.constrainWidth(v.id, 8.dp(context))
        constraints.constrainHeight(v.id, this.height)

        when (pos) {
            0 -> {
                constraints.setMargin(v.id, ConstraintSet.START, 0.dp(context))
                constraints.setMargin(v.id, ConstraintSet.END, 2.dp(context))
            }
            in 1 until (visualization?.bars?.lastIndex ?: 2) -> {
                constraints.setMargin(v.id, ConstraintSet.END, 2.dp(context))
            }
            visualization?.bars?.lastIndex -> {
                constraints.setMargin(v.id, ConstraintSet.END, 0.dp(context))
            }
        }
        constraints.applyTo(this)
    }

    fun animateBars() {
        visualization?.visualize()
    }

    fun stop() {
        visualization?.stopVisualization()
    }
}