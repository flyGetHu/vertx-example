package com.vertx.breaker.handler

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestBreakerHandlerImplTest {

    @Test
    fun test(vertx: Vertx, testContext: VertxTestContext) {
        val testBreakerHandlerImpl = TestBreakerHandlerImpl()
        CoroutineScope(vertx.dispatcher()).launch {
            val res = testBreakerHandlerImpl.execute(vertx)
            println("res: $res")
            testContext.completeNow()
        }
        testContext.awaitCompletion(10, java.util.concurrent.TimeUnit.SECONDS)
    }
}