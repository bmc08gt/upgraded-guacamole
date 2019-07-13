package dev.bmcreations.guacamole.ui.widgets.visualization

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

internal class SampledVisualization(context: Context, attributeSet: AttributeSet?): Visualization(context, attributeSet) {

    var playingSet: AnimatorSet? = null
    var pauseSet: AnimatorSet? = null

    init {
        setupBars()
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
            }.also {  it.start() }
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
        val steps  = FloatArray(26) { from + random.nextFloat() * (to - from) }
        return ObjectAnimator.ofFloat(v, "scaleY", *steps).apply {
            this.repeatCount = ValueAnimator.INFINITE
        }
    }
}