package dev.bmcreations.guacamole.operator

import kotlinx.coroutines.*

fun <E, T> paged(
    pagedCall: ((next: String?) -> Deferred<T>),
    next: String? = null,
    scope: CoroutineScope,
    onPage: (scope: CoroutineScope, items: List<E>) -> Unit,
    take: Int = 0
) {
     scope.launch {
         when (val nextPage = pagedCall.invoke(next).await()) {
             is dev.bmcreations.guacamole.models.PagedListImpl<*> -> {
                 if (take.coerceAtLeast(0) > 0) {
                     if (nextPage.next != null) {
                         paged(
                             pagedCall,
                             nextPage.next,
                             scope,
                             onPage,
                             take.coerceAtLeast(1) - 1
                         )
                     }
                     onPage.invoke(scope, nextPage.data as? List<E> ?: emptyList())
                 }
             }
             else -> onPage.invoke(scope, nextPage as List<E>)
         }
     }
}
