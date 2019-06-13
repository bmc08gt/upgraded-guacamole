package dev.bmcreations.guacamole.ui.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView



class SpacesItemDecoration(private val space: Int, private val orientation: Int) : RecyclerView.ItemDecoration() {

    var bottomOnly: Boolean = false
    var footer: Boolean = false
        set(value) {
            if (value) {
                if (!bottomOnly) {
                    bottomOnly = true
                }
            }
            field = value
        }
    var topBottomOnly: Boolean = false
    var header: Boolean = false
    var endCaps: Boolean = false

    var grid: Boolean? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (bottomOnly) {
            // Only add bottom padding/margin if vertical
            if (orientation == OrientationHelper.VERTICAL) {
                if (footer) {
                    val adapter = parent.adapter
                    adapter?.let {
                        if (grid == true) {
                            val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 2
                            if (parent.getChildAdapterPosition(view) >= adapter.lastPosition - spanCount) {
                                outRect.bottom = space
                            }
                        } else {
                            val position = parent.getChildAdapterPosition(view)
                            if (position == it.lastPosition) {
                                outRect.bottom = space
                            }
                        }
                    }
                } else {
                    outRect.bottom = space
                }
            }
        } else {
            if (endCaps) {
                when {
                    grid == true -> {
                        val adapter = parent.adapter
                        adapter?.let {
                            val lp = view.layoutParams as GridLayoutManager.LayoutParams
                            val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 2
                            val spanIndex = lp.spanIndex
                            if (spanIndex == 0) {
                                outRect.left = space
                            } else if (spanIndex == spanCount - 1) {
                                outRect.right = space
                            }
                        }
                    }
                    orientation == OrientationHelper.VERTICAL -> {
                        outRect.left = space
                        outRect.right = space
                    }
                    orientation == OrientationHelper.HORIZONTAL -> {
                        val adapter = parent.adapter
                        adapter?.let {
                            val position = parent.getChildAdapterPosition(view)
                            if (position == it.lastPosition) {
                                outRect.right = space
                            } else if (position == 0) {
                                outRect.left = space
                            }
                        }
                    }
                }
            } else {
                if (!topBottomOnly) {
                    outRect.left = space
                    outRect.right = space
                }
                if (header) {
                    if (grid == true) {
                        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 2
                        if (parent.getChildAdapterPosition(view) < spanCount) {
                            outRect.top = space
                        }
                    } else if (parent.getChildAdapterPosition(view) == 0 && orientation == OrientationHelper.VERTICAL) {
                        outRect.top = space
                    }
                } else {
                    if (grid == true) {
                        val adapter = parent.adapter
                        adapter?.let {
                            val lp = view.layoutParams as GridLayoutManager.LayoutParams
                            val spanIndex = lp.spanIndex
                            when (spanIndex) {
                                0 -> outRect.right = space
                                else -> outRect.left = space
                            }
                        }
                    } else {
                        outRect.top = space
                        outRect.bottom = space
                    }
                }
            }
        }
    }
}

val RecyclerView.Adapter<*>.lastPosition: Int
    get() {
        return this.itemCount - 1
    }

fun RecyclerView.addItemDecorations(vararg decorations: RecyclerView.ItemDecoration) {
    decorations.forEach { this.addItemDecoration(it) }
}