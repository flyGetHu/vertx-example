package com.vertx.breaker.handler

import cn.hutool.log.StaticLog
import com.vertx.breaker.enums.CircuitBreakerEnum
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.circuitbreaker.RetryPolicy
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
     * @param resetTimeout 熔断器打开后,设置尝试重新闭合电路（通过进入半开状态）之前的时间（以毫秒为单位）。
     *                     如果达到超时时电路关闭，则不会发生任何事情。 -1禁用此功能。
     * @param metricsRollingWindow 指标的默认滚动窗口大小,单位毫秒,默认为10000
     * @param metricsRollingBuckets 指标的滚动窗口的桶数,默认为10
     * @param failuresRollingWindow 失败的滚动窗口大小,单位毫秒,默认为10000,该值必须小于等于metricsRollingWindow
     * @param vertx Vertx实例,默认为com.vertx.common.config.vertx
     */
    suspend fun <T> execute(
        circuitBreakerEnum: CircuitBreakerEnum,
        action: suspend () -> T,
        fallback: (Throwable) -> T,
        timeout: Long = 10000,
        maxRetries: Int = 0,
        maxFailures: Int = 5,
        failuresRollingWindow: Long = 10000,
        resetTimeout: Long = 30000,
        metricsRollingWindow: Long = 10000,
        metricsRollingBuckets: Int = 10,
        vertx: Vertx = com.vertx.common.config.vertx
    ): T {
        if (resetTimeout < 1) throw IllegalArgumentException("resetTimeout must be greater than 0")
        if (timeout < 1) throw IllegalArgumentException("timeout must be greater than 0")
        if (maxRetries < 0) throw IllegalArgumentException("maxRetries must be greater than or equal to 0")
        if (maxFailures < 1) throw IllegalArgumentException("maxFailures must be greater than 0")
        if (metricsRollingWindow < 1) throw IllegalArgumentException("metricsRollingWindow must be greater than 0")
        if (metricsRollingBuckets < 1) throw IllegalArgumentException("metricsRollingBuckets must be greater than 0")
        if (failuresRollingWindow < 1) throw IllegalArgumentException("failuresRollingWindow must be greater than 0")
        if (failuresRollingWindow > metricsRollingWindow) throw IllegalArgumentException("failuresRollingWindow must be less than or equal to metricsRollingWindow")
        val name = circuitBreakerEnum.name.lowercase()
        val breaker = getCircuitBreaker(
            name,
            timeout,
            maxRetries,
            maxFailures,
            resetTimeout,
            metricsRollingWindow,
            metricsRollingBuckets,
            failuresRollingWindow,
            vertx = vertx
        )
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
        return breaker.execute {
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


    /**
     * 断路器Map
     * key: 断路器名称
     * value: 断路器
     * 作用: 用于存储断路器
     */
    private val CircuitBreakerMap = mutableMapOf<String, CircuitBreaker>()

    /**
     * 获取断路器
     */
    private fun getCircuitBreaker(
        name: String,
        timeout: Long,
        maxRetries: Int,
        maxFailures: Int,
        resetTimeout: Long,
        metricsRollingWindow: Long,
        metricsRollingBuckets: Int,
        failuresRollingWindow: Long, vertx: Vertx = com.vertx.common.config.vertx
    ): CircuitBreaker {
        return CircuitBreakerMap.getOrElse(name, defaultValue = {
            val circuitBreaker = CircuitBreaker.create(
                name,
                vertx,
                CircuitBreakerOptions()
                    .setTimeout(timeout)
                    .setMaxRetries(maxRetries)
                    .setMaxFailures(maxFailures)
                    .setResetTimeout(resetTimeout)
                    .setMetricsRollingWindow(metricsRollingWindow)
                    .setMetricsRollingBuckets(metricsRollingBuckets)
                    .setFailuresRollingWindow(failuresRollingWindow)
                    .setFallbackOnFailure(true)
            )
            circuitBreaker.retryPolicy(RetryPolicy.exponentialDelayWithJitter(50, 500))
            CircuitBreakerMap[name] = circuitBreaker
            circuitBreaker
        })
    }
}