package dev.bmcreations.guacamole.ui.browse

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.format
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment
import kotlinx.android.synthetic.main.fragment_library.view.*
import kotlinx.android.synthetic.main.fragment_user_content.*
import java.util.*

class BrowseFragment : NavigationStackFragment() {

    override val layoutResId get() = R.layout.fragment_browse

    override fun initView() {
        enableToolbarTranslationEffects(true)
        showToolbarElevation(false)

        root.scrollview.setScrollViewCallbacks(object : ObservableScrollViewCallbacks {
            override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) = Unit

            override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
                val dy = onScrollChange(scrollY, firstScroll, dragging)
                showToolbarElevation(show = (dy ?: 0f) >= 0)
            }

            override fun onDownMotionEvent() = Unit

        })
    }
}
