package com.vertx.common.handler

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.core.Vertx

/**
 * CircuitBreakerHandler
 * @param circuitBreaker
 * @constructor
 * @param timeout 500 ms 超时时间
 * @param maxFailures 3 最大失败次数
 * @param fallbackOnFailure true 失败时是否回退
 * @param resetTimeout 200 ms 重置时间
 */
data class CircuitBreakerHandler(
    val circuitBreakerName: String,
    val timeout: Long,
    val maxFailures: Int = 3,
    val fallbackOnFailure: Boolean = true,
    val resetTimeout: Long = 200
)

fun CircuitBreakerHandler.new(vertx: Vertx? = null): CircuitBreaker {
    val circuitBreakerOptions = CircuitBreakerOptions()
    circuitBreakerOptions.setMaxFailures(maxFailures)
    circuitBreakerOptions.setTimeout(timeout)
    circuitBreakerOptions.setFallbackOnFailure(fallbackOnFailure)
    circuitBreakerOptions.setResetTimeout(resetTimeout)
    var vertxParam = vertx
    if (vertx == null) {
        vertxParam = Vertx.currentContext().owner()
    }
    return CircuitBreaker.create(circuitBreakerName, vertxParam, circuitBreakerOptions)
}


