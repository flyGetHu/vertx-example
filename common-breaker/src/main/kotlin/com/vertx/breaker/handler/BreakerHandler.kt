package com.vertx.breaker.handler

import cn.hutool.log.StaticLog
import com.vertx.breaker.config.CircuitBreakerMap
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
 * @param Response 响应类型
 * @property responseClass Class<Response> 响应类型
 */
interface BreakerHandler<Response> {


    /**
     * The class of the request
     */
    val responseClass: Class<Response>


    /**
     * 执行超时时间
     */
    val timeout: Long

    /**
     * 在熔断器（Circuit Breaker）的概念中，"maxFailures"（最大失败次数）是一个重要的配置参数。熔断器的主要目标是监控外部系统的调用，
     * 并在外部系统出现连续失败时"短路"，以避免对外部系统的进一步调用，从而保护系统免受过多的负面影响。
     *
     * 具体来说，在一个熔断器中，"maxFailures" 配置表示在多少次连续失败的调用后，熔断器会打开（进入打开状态）。在打开状态下，
     * 熔断器会拒绝一段时间内的所有调用，以便外部系统有时间来恢复。
     *
     * 一旦达到 "maxFailures" 的失败次数，熔断器将开启以下流程：
     *
     * 1. 打开状态（Open）：在打开状态下，所有的外部调用都会立即失败，而不会尝试执行实际的操作。这有助于减轻外部系统的负担，避免进一步的失败。
     *
     * 2. 重置定时器（Reset Timer）：在熔断器进入打开状态后，会启动一个重置定时器，该定时器在一段预定的时间内运行。一旦定时器到期，熔断器将进入半开状态（Half-Open）。
     *
     * 3. 半开状态（Half-Open）：在半开状态下，熔断器会允许一个测试调用来判断外部系统是否已经恢复。如果测试调用成功，熔断器将进入关闭状态（Closed），如果失败，熔断器将再次返回打开状态。
     *
     * "maxFailures" 参数的设置非常重要，它应该根据您的系统和外部系统的特性来决定。如果设置得太小，熔断器可能会过于敏感，频繁地打开和关闭，影响正常的操作。
     * 如果设置得太大，熔断器可能无法及时响应外部系统的故障，从而影响了系统的健壮性。
     *
     * 因此，您应该根据您的应用需求和外部系统的行为模式来选择合适的 "maxFailures" 值，以确保系统能够在故障情况下保持可用性，并在外部系统恢复后逐渐恢复正常操作。
     */
    val maxFailures: Int

    /**
     * `resetTimeout` 是熔断器（Circuit Breaker）中的一个配置参数，用于设置断路器从打开状态（Open）转换为半开状态（Half-Open）的时间间隔。
     * 在半开状态下，断路器会允许执行一个测试调用来判断外部系统是否已经恢复，从而确定是否应该继续尝试正常操作。
     *
     * 具体来说，`resetTimeout` 参数指定了断路器保持打开状态的时间。一旦达到这个时间，断路器会自动进入半开状态，以便进行一次测试调用来检查外部系统的恢复情况。
     *
     * 例如，假设您将 `resetTimeout` 设置为 5000 毫秒（5 秒），并且在某个时间点断路器因连续失败而打开。在接下来的 5 秒内，断路器将保持打开状态，
     * 不会尝试执行任何正常操作。然后，在 5 秒后，断路器会进入半开状态，允许执行一个测试调用来确定外部系统是否已经恢复。
     *
     * 如果测试调用成功，断路器将重置为关闭状态（Closed），并重新开始允许正常操作。如果测试调用失败，断路器将返回到打开状态，继续保持一段时间。
     *
     * 设置适当的 `resetTimeout` 对于平衡系统的健壮性和故障恢复非常重要。如果将 `resetTimeout` 设置得太短，系统可能会频繁地从打开状态切换到半开状态，
     * 导致系统在外部系统恢复之前无法正常工作。如果将 `resetTimeout` 设置得太长，系统可能会在外部系统恢复后需要更长的时间来重新启用正常操作。
     *
     * 因此，您应该根据您的应用需求和外部系统的恢复速度来选择合适的 `resetTimeout` 值，以确保系统在故障情况下能够适当地恢复并保持稳定。
     */
    val resetTimeout: Long

    /**
     * 最大重试次数
     */
    val maxRetries: Int

    /**
     *  回退函数
     */
    fun fallback(e: Throwable): Response

    /**
     * 主要业务逻辑
     */
    suspend fun action(): Response

    suspend fun execute(vertx: Vertx = com.vertx.common.config.vertx): Response {
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
        breaker.fallback { e ->
            StaticLog.error(e, "circuitBreaker fallback error name: $name")
            fallback(e)
        }
        return breaker.execute<Response> {
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