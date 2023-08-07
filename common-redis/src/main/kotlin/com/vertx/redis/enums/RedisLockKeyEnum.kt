package com.vertx.redis.enums

/**
 * 命名规则：LOCK_业务名称, 例如LOCK_TEST,多个单词用下划线分割
 * redis分布式锁的key枚举类
 * 用于redis分布式锁的key
 */
enum class RedisLockKeyEnum {
    LOCK_TEST,
}