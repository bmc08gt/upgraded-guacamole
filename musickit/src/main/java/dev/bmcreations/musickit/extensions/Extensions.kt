package dev.bmcreations.musickit.extensions

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 */
inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

/**
 * Returns the first element, or `null` if the list is empty.
 */
fun <T> List<T>.randomOrNull(): T? {
    return if (isEmpty()) null else this.random()
}
