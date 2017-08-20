# kotlin-coroutine-builders
Sample builders that can be used to launch coroutines (example implementation of async, await, delay, runBlocking)

The most common Uncondifed, CommonPool and Swing CoroutineInterceptors have been implemented.

```kotlin
    fun workUsingFutures(result: Int): CompletableFuture<Int> = async(CommonPool) {
        delay(1000)
        result
    }

    @Test
    fun sample() {
        val result = runBlocking {
            val futureList = mutableListOf<CompletableFuture<Int>>()
            for (i in 1..100_000) {
                futureList.add(workUsingFutures(1))
            }
            futureList.sumBy { it.await() }
        }

        assertEquals(100_000, result)
    }
```
