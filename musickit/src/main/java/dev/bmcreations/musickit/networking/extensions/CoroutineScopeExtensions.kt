package dev.bmcreations.musickit.networking.extensions

import kotlinx.coroutines.*
import java.util.*

private val job = Job()
val uiScope  = CoroutineScope(Dispatchers.Main + job)

fun at(date: Date, callback: (() -> Unit)): Job {
    return at(date.time, callback)
}

fun at(timestamp: Long, callback: (() -> Unit)): Job {
    var waitTime = (timestamp - System.currentTimeMillis())
    if (waitTime < 0) waitTime = 0
    return uiScope.launch {
        delay(waitTime)
        callback.invoke()
    }
}

fun inTime(offset: Long, callback: (() -> Unit)): Job {
    return uiScope.launch {
        delay(offset)
        callback.invoke()
    }
}