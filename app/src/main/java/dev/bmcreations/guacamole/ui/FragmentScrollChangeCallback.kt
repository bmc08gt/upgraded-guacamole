package dev.bmcreations.guacamole.ui

interface FragmentScrollChangeCallback {
    fun setTitle(title: String?)
    fun showElevation(show: Boolean)
    fun onScrollChange(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float
    fun enableScrollChange(enable: Boolean)
}
