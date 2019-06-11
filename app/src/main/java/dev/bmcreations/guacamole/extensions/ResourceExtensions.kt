package dev.bmcreations.guacamole.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getDrawableForDensity
import androidx.core.content.res.ResourcesCompat.getFont

val Context.animations
    get() = ResourceMapper { resources.getAnimation(it) }
val Context.booleans
    get() = ResourceMapper { resources.getBoolean(it) }
val Context.colors
    get() = ResourceMapper { ContextCompat.getColor(this, it) }

val Context.colorStateLists
    get() = ResourceMapper { ContextCompat.getColorStateList(this, it) }
val Context.dimens
    get() = ResourceMapper { resources.getDimension(it) }
val Context.dimensInt
    get() = ResourceMapper { resources.getDimensionPixelSize(it) }
val Context.dimensOffset
    get() = ResourceMapper { resources.getDimensionPixelOffset(it) }
val Context.drawables
    get() = ResourceMapper { getDrawable(it) }
val Context.scaledDrawable
    get() = ResourceMapper { ScaledDrawable(resources, it, theme) }
val Context.fonts
    get() = ResourceMapper { getFont(this, it)!! }
val Context.intArrays
    get() = ResourceMapper { resources.getIntArray(it) }
val Context.ints
    get() = ResourceMapper { resources.getInteger(it) }
val Context.layouts
    get() = ResourceMapper { resources.getLayout(it) }
val Context.movies
    get() = ResourceMapper { resources.getMovie(it) }
val Context.formattedStrings
    get() = ResourceMapper { FormattedString(resources, it) }
val Context.resourceInfos
    get() = ResourceMapper { ResourceInfo(resources, it) }
val Context.strings
    get() = ResourceMapper { getString(it) }
val Context.stringArrays
    get() = ResourceMapper { resources.getStringArray(it) }
val Context.texts
    get() = ResourceMapper { getText(it) }
val Context.textArrays
    get() = ResourceMapper { resources.getTextArray(it) }
val Context.xmls
    get() = ResourceMapper { resources.getXml(it) }
val Context.typedArrays
    get() = ResourceMapper { return@ResourceMapper resources.obtainTypedArray(it) }
val Context.rawResources
    get() = ResourceMapper { resources.openRawResource(it) }

interface ContextAware {
    fun getContext(): Context
}

val ContextAware.animations get() = getContext().animations
val ContextAware.booleans get() = getContext().booleans
val ContextAware.colors get() = getContext().colors
val ContextAware.colorStateLists get() = getContext().colorStateLists
val ContextAware.dimens get() = getContext().dimens
val ContextAware.dimensInt get() = getContext().dimensInt
val ContextAware.dimensOffset get() = getContext().dimensOffset
val ContextAware.drawables get() = getContext().drawables
val ContextAware.scaledDrawable get() = getContext().scaledDrawable
val ContextAware.fonts get() = getContext().fonts
val ContextAware.intArrays get() = getContext().intArrays
val ContextAware.ints get() = getContext().ints
val ContextAware.layouts get() = getContext().layouts
val ContextAware.movies get() = getContext().movies
val ContextAware.formattedStrings get() = getContext().formattedStrings
val ContextAware.resourceInfos get() = getContext().resourceInfos
val ContextAware.strings get() = getContext().strings
val ContextAware.stringArrays get() = getContext().stringArrays
val ContextAware.texts get() = getContext().texts
val ContextAware.textArrays get() = getContext().textArrays
val ContextAware.xmls get() = getContext().xmls
val ContextAware.typedArrays get() = getContext().typedArrays
val ContextAware.rawResources get() = getContext().rawResources

class FormattedString(
    private val resources: Resources,
    private val resId: Int
) {
    operator fun invoke(vararg values: Any): String =
        resources.getString(resId, *values)

    operator fun invoke(quantity: Int): String =
        resources.getQuantityString(resId, quantity)

    operator fun invoke(quantity: Int, vararg values: Any): String =
        resources.getQuantityString(resId, quantity, *values)
}

data class ResourceInfo(
    private val resources: Resources,
    private val resId: Int,
    val entryName: String = resources.getResourceEntryName(resId),
    val name: String = resources.getResourceName(resId),
    val packageName: String = resources.getResourcePackageName(resId),
    val typeName: String = resources.getResourceTypeName(resId)
)

class ScaledDrawable(
    private val resources: Resources,
    private val resId: Int,
    private val theme: Resources.Theme
) {
    operator fun invoke(density: Int): Drawable =
        getDrawableForDensity(resources, resId, density, theme)!!
}

class ResourceMapper<out T>(private val mapRes: (resId: Int) -> T) {
    operator fun get(@AnyRes resId: Int) = mapRes(resId)
}