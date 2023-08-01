/**
 * CircuitBreakerHandler data class represents a circuit breaker configuration.
 * @property circuitBreakerName the name of the circuit breaker.
 * @property timeout the timeout value in milliseconds.
 * @property maxFailures the maximum number of failures before opening the circuit.
 * @property fallbackOnFailure a boolean value indicating whether to fallback on failure.
 * @property resetTimeout the time in milliseconds to wait before attempting to close the circuit again.
 */
package com.vertx.common.handler

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.core.Vertx

/**
 * CircuitBreakerHandler
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
    val resetTimeout: Long = 10000
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


