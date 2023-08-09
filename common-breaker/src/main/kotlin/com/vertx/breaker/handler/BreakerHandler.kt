package com.vertx.breaker.handler

import cn.hutool.log.StaticLog
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.circuitbreaker.RetryPolicy
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * 断路器Map
 * key: 断路器名称
 * value: 断路器
 * 作用: 用于存储断路器
 */
val CircuitBreakerMap = mutableMapOf<String, CircuitBreaker>()

/**
 * 断路器处理器
 * 作用: 用于处理断路器相关逻辑
 */
object BreakerHandler {
    /**
     * 熔断器执行函数
     * @param action 主要业务逻辑
     * @param fallback 回退函数,非suspend函数
     * @param timeout 主要业务逻辑的超时时间
     * @param maxRetries 最大重试次数
     * @param maxFailures 最大失败次数,超过该次数后,熔断器将会打开,并且在resetTimeout时间后尝试关闭
     * @param resetTimeout 熔断器打开后,尝试关闭的时间
     * @param vertx Vertx实例,默认为com.vertx.common.config.vertx
     */
    suspend fun <T> execute(
        action: suspend () -> T,
        fallback: (Throwable) -> T,
        timeout: Long = 10000,
        maxRetries: Int = 0,
        maxFailures: Int = 5,
        resetTimeout: Long = 30000,
        vertx: Vertx = com.vertx.common.config.vertx
    ): T {
        val name = this::class.java.name
        val breaker = CircuitBreakerMap.getOrElse(name, defaultValue = {
            val circuitBreaker = CircuitBreaker.create(
                name,
                vertx,
                CircuitBreakerOptions().setMaxFailures(maxFailures).setResetTimeout(resetTimeout)
                    .setMaxRetries(maxRetries).setTimeout(timeout).setFallbackOnFailure(true)
            )
            circuitBreaker.retryPolicy(RetryPolicy.exponentialDelayWithJitter(50, 500))
            CircuitBreakerMap[name] = circuitBreaker
            circuitBreaker
        })
        breaker.closeHandler {
            StaticLog.info("circuitBreaker close name: $name")
        }
        breaker.openHandler {
            StaticLog.info("circuitBreaker open name: $name")
        }
        breaker.halfOpenHandler {
            StaticLog.info("circuitBreaker halfOpen name: $name")
        }
        breaker.fallback { e ->
            StaticLog.error(e, "circuitBreaker fallback error name: $name")
            fallback(e)
        }
        return breaker.execute<T> {
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    it.complete(action())
                } catch (e: Throwable) {
                    StaticLog.error(e, "circuitBreaker execute error name: $name")
                    it.fail(e)
                }
            }
        }.await()
    }
}