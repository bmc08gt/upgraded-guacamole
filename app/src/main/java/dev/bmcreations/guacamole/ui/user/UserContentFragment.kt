package dev.bmcreations.guacamole.ui.user

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.format
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment
import kotlinx.android.synthetic.main.fragment_library.view.*
import kotlinx.android.synthetic.main.fragment_user_content.*
import java.util.*

class UserContentFragment: NavigationStackFragment() {

    override val layoutResId get() = R.layout.fragment_user_content

    override fun initView() {
        enableToolbarTranslationEffects(true)
        showToolbarElevation(false)

        today_date.text = Date().format(format = "EEEE, MMM dd")

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
