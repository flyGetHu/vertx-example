package com.vertx.breaker.handler

import cn.hutool.core.thread.ThreadUtil
import kotlinx.coroutines.delay

class TestBreakerHandlerImpl : BreakerHandler<String> {
    override val responseClass: Class<String>
        get() = String::class.java

    override val timeout: Long
        get() = 500
    override val maxFailures: Int
        get() = 5
    override val resetTimeout: Long
        get() = 15000
    override val maxRetries: Int
        get() = 5

    override fun fallback(e: Throwable): String {
        println("fallback")
        ThreadUtil.sleep(2000)
        return "fallback"
    }

    override suspend fun action(): String {
        println("action")
        delay(1000)
        return "action"
    }
}