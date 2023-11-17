package com.vertx.redis.exception

/**
 * 针对Redis初始化错误的自定义异常类。
 * 扩展标准异常类。
 *
 * @param message 描述异常的错误消息。
 * @param cause 异常的原因。如果原因未知或不存在，则它可以为 null。
 */
class RedisInitException(message: String, cause: Throwable? = null) : Exception(message, cause)