package dev.bmcreations.guacamole.ui.widgets

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.use
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.colors
import dev.bmcreations.guacamole.extensions.dp
import dev.bmcreations.guacamole.extensions.handleLayout
import dev.bmcreations.guacamole.extensions.isVisible
import java.util.*

@SuppressLint("Recycle")
class EqualizerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {

    companion object {
        const val BAR_COUNT = 3
    }

    private val bars = mutableListOf<View>()

    var group = Group(context, attrs)

    var color: Int = context.colors[R.color.colorAccent]
    var duration: Long = 3000L

    var animating = false
    var playingSet: AnimatorSet? = null
    var pauseSet: AnimatorSet? = null

    init {
        ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
            group.layoutParams = it
        }

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

        bars.clear()
        for (i in 0 until BAR_COUNT) {
            val b = View(context, attrs)
            bars.add(b)
        }

        updateBars()
    }

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

        bars.forEachIndexed { i, v ->
            v.id = View.generateViewId()
            v.setBackgroundColor(color)
            v.handleLayout {
                v.pivotY = this.height.toFloat() - this.paddingBottom - this.paddingTop
            }
            addView(v, i)
            setConstraints(i, v)
        }

        group.referencedIds = bars.map { it.id }.toIntArray()

        val constraints = ConstraintSet()
        constraints.clone(this)
        constraints.createHorizontalChain(
            ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
            group.referencedIds, null, ConstraintSet.CHAIN_SPREAD_INSIDE
        )
        constraints.applyTo(this)
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
            in 1 until bars.lastIndex -> {
                constraints.setMargin(v.id, ConstraintSet.END, 2.dp(context))
            }
            bars.lastIndex -> {
                constraints.setMargin(v.id, ConstraintSet.END, 0.dp(context))
            }
        }
        constraints.applyTo(this)
    }

    fun animateBars() {
        animating = true
        playingSet?.let {
            if (it.isPaused) {
                it.resume()
            }
        } ?: run {
            val animators = mutableListOf<ObjectAnimator>()
            bars.forEach { b -> animators.add(generateBarScalar(b)) }

            playingSet = AnimatorSet().apply {
                this.playTogether(*animators.toTypedArray())
                this.duration = this@EqualizerView.duration
                this.interpolator = LinearInterpolator()
            }.also {  it.start() }
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

    fun stop() {
        animating = false
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
}