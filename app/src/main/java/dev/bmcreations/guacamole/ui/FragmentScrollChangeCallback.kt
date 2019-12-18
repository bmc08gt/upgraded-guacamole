package dev.bmcreations.guacamole.ui

interface FragmentScrollChangeCallback {
    fun showElevation(show: Boolean)
    fun onScrollChange(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float
}
