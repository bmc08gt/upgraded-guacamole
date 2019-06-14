package dev.bmcreations.musickit.networking.operator

import kotlin.reflect.KProperty

class FieldProperty<R, T : Any>(
    val initializer: (R) -> T = { throw IllegalStateException("Not initialized.") }
) {
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
        map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }
}

/**
 * Provides property delegation which behaves as if each [R] instance had a backing field of type [T] for that property.
 * Delegation can be defined at top level or inside a class, which will mean that the delegation is scoped to
 * instances of the class -- separate instances will see separate values of the delegated property.
 *
 * This implementation is not thread-safe. Use [SynchronizedNullableFieldProperty] for thread-safe delegation.
 *
 * This delegate allows `null` values.
 *
 * If the delegated property of an [R] instance is accessed but has not been initialized, [initializer] is called to
 * provide the initial value. The default [initializer] returns `null`.
 */
class NullableFieldProperty<R, T>(val initializer: R.() -> T? = { null }) {
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T? =
        if (thisRef in map) map[thisRef] else setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T?): T? {
        map[thisRef] = value
        return value
    }
}