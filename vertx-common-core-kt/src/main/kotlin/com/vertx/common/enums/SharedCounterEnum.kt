package com.vertx.common.enums

/**
 * 表示具有唯一密钥的共享计数器。
 *
 * @property key 共享计数器的唯一键。
 */
enum class SharedCounterEnum(var key: String) {
    TEST_SHARED_COUNTER("test_counter"),
}