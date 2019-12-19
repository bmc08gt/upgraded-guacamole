package dev.bmcreations.musickit.networking.extensions

import kotlinx.coroutines.*
import java.util.*

fun at(date: Date, callback: (() -> Unit)): Job {
    return at(timestamp = date.time, callback = callback)
}

fun at(scope: CoroutineScope = CoroutineScope(Dispatchers.Main), timestamp: Long, callback: (() -> Unit)): Job {
    var waitTime = (timestamp - System.currentTimeMillis())
    if (waitTime < 0) waitTime = 0
    return scope.launch {
        delay(waitTime)
        callback.invoke()
    }
}

fun inTime(scope: CoroutineScope = CoroutineScope(Dispatchers.Main), offset: Long, callback: (() -> Unit)): Job {
    return scope.launch {
        delay(offset)
        callback.invoke()
    }
}
