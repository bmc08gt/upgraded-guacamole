package dev.bmcreations.guacamole.operator

import androidx.lifecycle.MutableLiveData
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
                     paged(
                         pagedCall,
                         nextPage.next,
                         scope,
                         results,
                         liveData
                     )
                 }
                 liveData.postValue(results)
             }
             else -> liveData.postValue(results)
         }
     }
}
