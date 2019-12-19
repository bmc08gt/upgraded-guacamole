package dev.bmcreations.guacamole.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.bmcreations.guacamole.extensions.strings
import org.jetbrains.anko.AnkoLogger

abstract class NavigationStackFragment : Fragment(), AnkoLogger {

    protected var fragmentScrollCallback: FragmentScrollChangeCallback? = null

    abstract val layoutResId: Int

    lateinit var root: View

    abstract fun initView()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentScrollChangeCallback) {
            fragmentScrollCallback = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(layoutResId, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun setToolbarTitle(titleResId: Int) {
        val title = context?.strings?.get(titleResId)
        fragmentScrollCallback?.setTitle(title)
    }

    fun setToolbarTitle(title: String?) {
        fragmentScrollCallback?.setTitle(title)
    }

    fun showToolbarElevation(show: Boolean) {
        fragmentScrollCallback?.showElevation(show)
    }

    fun enableToolbarTranslationEffects(enable: Boolean) {
        fragmentScrollCallback?.enableScrollChange(enable)
    }

    fun onScrollChange(scrollY: Int, firstScroll: Boolean, dragging: Boolean): Float? {
        return fragmentScrollCallback?.onScrollChange(scrollY, firstScroll, dragging)
    }
}
