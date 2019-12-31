package dev.bmcreations.guacamole.extensions

/**
 * Returns the first element, or `null` if the list is empty.
 */
fun <T> List<T>.randomOrNull(): T? {
    return if (isEmpty()) null else this.random()
}
