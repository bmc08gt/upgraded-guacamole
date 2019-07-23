package dev.bmcreations.guacamole.ui.widgets.visualization

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.handleLayout
import java.util.*

internal class SampledVisualization(context: Context, attributeSet: AttributeSet?) :
    Visualization(context, attributeSet) {

    companion object {
        const val BAR_COUNT = 3
    }

    val bars = mutableListOf<View>()

    var playingSet: AnimatorSet? = null
    var pauseSet: AnimatorSet? = null

    var group = Group(context, attrs)

    init {
        setup()
    }

    override fun setup() {
        bars.clear()
        for (i in 0 until BAR_COUNT) {
            val b = View(context, attrs)
            bars.add(b)
        }
        ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).also {
            group.layoutParams = it
        }
    }

    override fun updateBars(onBar: (View, Int) -> Unit) {
        bars.forEachIndexed { index, bar ->
            bar.id = generateViewId()
            bar.setBackgroundColor(color)
            bar.handleLayout {
                bar.pivotY = this.height.toFloat() - this.paddingBottom
            }
            onBar.invoke(bar, index)
        }
    }

    override fun setConstraints(parent: ConstraintLayout) {
        super.setConstraints(parent)

        val constraints = ConstraintSet()
        constraints.clone(parent)

        group.referencedIds = bars.map { it.id }.toIntArray()

        var previous: View? = null
        bars.forEachIndexed { index, bar ->
            constraints.clear(bar.id)
            constraints.constrainWidth(bar.id, parent.width / bars.size)
            constraints.connect(bar.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
            constraints.connect(bar.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)
            if (previous == null) {
                constraints.connect(bar.id, ConstraintSet.LEFT, parent.id, ConstraintSet.LEFT)
            } else {
                constraints.connect(bar.id, ConstraintSet.LEFT, previous!!.id, ConstraintSet.RIGHT, 2.dp(context))
                if (bars.lastIndex == index) {
                    constraints.connect(bar.id, ConstraintSet.RIGHT, parent.id, ConstraintSet.RIGHT)
                }
            }
            previous = bar
        }
        constraints.createHorizontalChain(
            ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
            group.referencedIds, null, ConstraintSet.CHAIN_SPREAD_INSIDE
        )
        constraints.applyTo(parent)
    }

    override fun visualize() {
        super.visualize()
        playingSet?.let {
            if (it.isPaused) {
                it.resume()
            }
        } ?: run {
            val animators = mutableListOf<ObjectAnimator>()
            bars.forEach { b -> animators.add(generateBarScalar(b)) }

            playingSet = AnimatorSet().apply {
                this.playTogether(*animators.toTypedArray())
                this.duration = this@SampledVisualization.duration
                this.interpolator = LinearInterpolator()
            }.also { it.start() }
        }
    }

    override fun stopVisualization() {
        super.stopVisualization()
        playingSet?.let {
            if (it.isRunning && it.isStarted) {
                it.pause()
            }
        }

        pauseSet?.let {
            if (!it.isStarted) {
                it.start()
            }
        } ?: run {
            // Animate stopping bars
            val animators = mutableListOf<ObjectAnimator>()
            bars.forEach { b -> animators.add(ObjectAnimator.ofFloat(b, "scaleY", 0.1f)) }

            pauseSet = AnimatorSet().apply {
                this.playTogether(*animators.toTypedArray())
                this.duration = 200
            }.also { it.start() }
        }
    }

    private fun generateBarScalar(v: View): ObjectAnimator {
        val to = 0.9f
        val from = 0.1f
        val random = Random()
        val steps = FloatArray(26) { from + random.nextFloat() * (to - from) }
        return ObjectAnimator.ofFloat(v, "scaleY", *steps).apply {
            this.repeatCount = ValueAnimator.INFINITE
        }
    }
}