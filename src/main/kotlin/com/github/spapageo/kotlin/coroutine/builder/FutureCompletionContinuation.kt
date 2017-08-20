package com.github.spapageo.kotlin.coroutine.builder

import java.util.concurrent.CompletableFuture
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext

class FutureCompletionContinuation<T>(override val context: CoroutineContext) : Continuation<T> {

    val future: CompletableFuture<T> = CompletableFuture()

    override fun resume(value: T) {
        future.complete(value)
    }

    override fun resumeWithException(exception: Throwable) {
        future.completeExceptionally(exception)
    }
}