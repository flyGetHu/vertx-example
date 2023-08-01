package com.vertx.common.handler

import cn.hutool.core.thread.ThreadUtil
import com.vertx.breaker.handler.CircuitBreakerHandler
import com.vertx.breaker.handler.new
import io.vertx.circuitbreaker.RetryPolicy
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class CircuitBreakerHandlerTest {
    @Test
    fun test1(vertx: Vertx, testContext: VertxTestContext) {
        //测试CircuitBreakerHandler
        val circuitBreakerHandler = CircuitBreakerHandler(
            circuitBreakerName = "test1",
            timeout = 500,
            maxFailures = 3,
            fallbackOnFailure = true,
            resetTimeout = 200
        ).new(vertx)
            .fallback {
                println("执行失败")
                testContext.completeNow()
            }.retryPolicy(RetryPolicy.exponentialDelayWithJitter(50, 500))
        circuitBreakerHandler.execute<String> {
            ThreadUtil.sleep(1000 * 10)
            println("执行成功")
            it.complete()
        }
        testContext.awaitCompletion(60, java.util.concurrent.TimeUnit.SECONDS)
    }
}
