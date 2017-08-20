package com.github.spapageo.kotlin.coroutine.builder

import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import javax.swing.SwingUtilities
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.ContinuationInterceptor
import kotlin.coroutines.experimental.CoroutineContext

class UnconfinedCoroutineDispatcher : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>) = continuation
}

class ExecutorServiceCoroutineDispatcher(private val executorService: ExecutorService) :
        AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return object : Continuation<T> {
            override val context: CoroutineContext
                get() = continuation.context + this@ExecutorServiceCoroutineDispatcher

            override fun resume(value: T) {
                executorService.submit {
                    continuation.resume(value)
                }
            }

            override fun resumeWithException(exception: Throwable) {
                executorService.submit {
                    continuation.resumeWithException(exception)
                }
            }

        }
    }
}

class SwingCoroutineDispatcher : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return object : Continuation<T> {
            override val context: CoroutineContext
                get() = continuation.context + this@SwingCoroutineDispatcher

            override fun resume(value: T) {
                SwingUtilities.invokeLater {
                    continuation.resume(value)
                }
            }

            override fun resumeWithException(exception: Throwable) {
                SwingUtilities.invokeLater {
                    continuation.resumeWithException(exception)
                }
            }

        }
    }
}

val Swing : ContinuationInterceptor = SwingCoroutineDispatcher()
val CommonPool : ContinuationInterceptor = ExecutorServiceCoroutineDispatcher(ForkJoinPool.commonPool())
val Unconfined : ContinuationInterceptor = UnconfinedCoroutineDispatcher()