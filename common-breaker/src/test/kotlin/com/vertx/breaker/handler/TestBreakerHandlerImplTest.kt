package com.vertx.breaker.handler

import cn.hutool.core.thread.ThreadUtil
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestBreakerHandlerImplTest {

    @Test
    fun test(vertx: Vertx, testContext: VertxTestContext) {
        CoroutineScope(vertx.dispatcher()).launch {
            for (i in 1..100) {
                val res = BreakerHandler.execute(
                    vertx = vertx,
                    timeout = 500,
                    maxRetries = 3,
                    action = {
                        println("action")
                        delay(1000)
                        "action"
                    },
                    fallback = {
                        println("fallback")
                        "fallback"
                    },
                )
                println("res: $res")
            }
        }
        ThreadUtil.sleep(10000)
    }
}