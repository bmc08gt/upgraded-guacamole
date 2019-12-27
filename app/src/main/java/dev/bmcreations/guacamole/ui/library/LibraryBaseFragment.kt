package dev.bmcreations.guacamole.ui.library

import dev.bmcreations.guacamole.extensions.getViewModel
import dev.bmcreations.guacamole.graph
import dev.bmcreations.guacamole.ui.navigation.NavigationStackFragment

open class LibraryBaseFragment: NavigationStackFragment() {

    val librarySource get() = context?.graph()?.networkGraph?.librarySource
    val musicQueue get() = context?.graph()?.sessionGraph?.musicQueue

    val vm by lazy {
        librarySource?.let { source ->
            musicQueue?.let { queue ->
                activity?.getViewModel { LibraryViewModel(source, queue) } }
        }
    }

    override val layoutResId: Int = 0 // subclasses initialize

    override fun initView() = Unit
}
