package com.github.spapageo.kotlin.coroutine.builder

import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class BuildersTest {
    fun workUsingFutures(result: Int): CompletableFuture<Int> = async(CommonPool) {
        delay(1000)
        result
    }

    @Test
    fun test() {
        val result = runBlocking {
            val futureList = mutableListOf<CompletableFuture<Int>>()
            for (i in 1..100_000) {
                futureList.add(workUsingFutures(1))
            }
            futureList.sumBy { it.await() }
        }

        assertEquals(100_000, result)
    }

}
