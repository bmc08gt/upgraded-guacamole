package dev.bmcreations.musickit.extensions

import dev.bmcreations.musickit.networking.api.models.PagedListImpl
import kotlinx.coroutines.*
import kotlin.coroutines.resume


suspend fun <E, T> paged(
    pagedCall: ((next: String?) -> Deferred<T>),
    next: String? = null,
    scope: CoroutineScope,
    results: MutableList<E> = mutableListOf()
): E? {
    return suspendCancellableCoroutine {
        scope.launch {
            when (val nextPage = pagedCall.invoke(next).await()) {
                is PagedListImpl<*> -> {
                    results.addAll(nextPage.data as? List<E> ?: emptyList())
                    if (nextPage.next != null) {
                        paged(pagedCall, nextPage.next, scope, results)
                    }
                    it.resume(results.toList() as? E)
                }
                else -> it.resume(nextPage as? E)
            }
        }
    }
}
