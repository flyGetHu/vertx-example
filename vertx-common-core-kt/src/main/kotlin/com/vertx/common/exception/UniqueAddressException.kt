package com.vertx.common.exception

/**
 * 尝试将重复地址或者丢失添加到地址集合时抛出异常。
 *
 * @constructor 创建一个没有消息或原因的 UniqueAddressException 的新实例。
 * @constructor 使用指定的消息创建 UniqueAddressException 的新实例。
 * @constructor 使用指定的消息和原因创建 UniqueAddressException 的新实例。
 * @constructor 创建具有指定原因的 UniqueAddressException 的新实例。
 */
class UniqueAddressException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}