package dev.bmcreations.musickit.extensions

import androidx.lifecycle.MutableLiveData
import dev.bmcreations.guacamole.models.LibrarySong
import dev.bmcreations.guacamole.models.PagedListImpl
import kotlinx.coroutines.*

fun <E, T> paged(
    pagedCall: ((next: String?) -> Deferred<T>),
    next: String? = null,
    scope: CoroutineScope,
    results: MutableList<E> = mutableListOf(),
    liveData: MutableLiveData<List<E>>
) {
     scope.launch {
         when (val nextPage = pagedCall.invoke(next).await()) {
             is dev.bmcreations.guacamole.models.PagedListImpl<*> -> {
                 results.addAll((nextPage.data as? List<E>) ?: emptyList())
                 if (nextPage.next != null) {
                     paged(pagedCall, nextPage.next, scope, results, liveData)
                 }
                 liveData.postValue(results)
             }
             else -> liveData.postValue(results)
         }
     }
}
