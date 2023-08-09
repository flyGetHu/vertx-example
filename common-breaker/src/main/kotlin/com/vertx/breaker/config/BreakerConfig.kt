package com.vertx.breaker.config

import io.vertx.circuitbreaker.CircuitBreaker

/**
 * 断路器Map
 * key: 断路器名称
 * value: 断路器
 * 作用: 用于存储断路器
 */
val CircuitBreakerMap = mutableMapOf<String, CircuitBreaker>()