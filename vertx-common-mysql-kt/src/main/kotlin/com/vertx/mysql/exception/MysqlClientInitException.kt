package com.vertx.rabbitmq.exception

/**
 * 向 RabbitMQ 发送消息时发生错误时抛出异常。
 *
 * @constructor 创建 [MysqlClientInitException] 的新实例，没有任何消息或原因。
 * @constructor 使用指定的消息创建 [MysqlClientInitException] 的新实例。
 * @param message 详细消息。
 * @constructor 使用指定的消息和原因创建 [MysqlClientInitException] 的新实例。
 * @param message 详细消息。
 * @param Cause 异常的原因。
 * @constructor 创建具有指定原因的 [MysqlClientInitException] 的新实例。
 * @param Cause 异常的原因。
 */
class MysqlClientInitException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}