package com.github.spapageo.kotlin.coroutine.builder

import java.util.concurrent.*
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED

fun <T> runBlocking(block: suspend () -> T): T = async(CommonPool, block).get()

fun <T> async(coroutineInterceptor: ContinuationInterceptor, block: suspend () -> T): CompletableFuture<T>  {
    val completionContinuation = FutureCompletionContinuation<T>(coroutineInterceptor)

    block.startCoroutine(completionContinuation)

    return completionContinuation.future
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> CompletableFuture<T>.await(): T = suspendCoroutine { continuation ->
    (continuation as Continuation<Any>).resume(COROUTINE_SUSPENDED)
    this.whenComplete { result, throwable ->
        if(result == null)
            continuation.resumeWithException(throwable)
        else
            continuation.resume(result)
    }
}

var globalScheduler: ScheduledExecutorService = ScheduledThreadPoolExecutor(4)

@Suppress("UNCHECKED_CAST")
suspend fun delay(time: Long) = suspendCoroutine<Unit> { continuation ->
    (continuation as Continuation<Any>).resume(COROUTINE_SUSPENDED)
    globalScheduler.schedule({
        continuation.resume(Unit)
    }, time, TimeUnit.MILLISECONDS)
}
