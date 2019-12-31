package dev.bmcreations.guacamole.ui.library

import android.os.Bundle
import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.media.MediaStateLifecycleObserver
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment

open class LibraryBaseFragment: NavigationStackFragment() {

    val librarySource get() = context?.graph()?.networkGraph?.librarySource
    val geniusSearch get() = context?.graph()?.networkGraph?.geniusSearchSource
    val mediaState get() = context?.graph()?.sessionGraph?.mediaState

    val vm by lazy {
        librarySource?.let { source ->
            geniusSearch?.let { genius ->
                activity?.getViewModel { LibraryViewModel(source, genius) }
            }
        }
    }

    override val layoutResId: Int = 0 // subclasses initialize

    override fun initView() = Unit
}
