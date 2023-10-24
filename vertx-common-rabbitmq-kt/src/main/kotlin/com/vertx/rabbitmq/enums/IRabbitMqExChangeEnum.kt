package com.vertx.rabbitmq.enums

/**
 * Represents an interface for RabbitMQ Exchange Enum.
 */
interface IRabbitMqExChangeEnum {
    // 交换机类型
    val type: RabbitMqExChangeTypeEnum

    // 交换机名称
    val exchanger: String

    // 消息类型
    val messageType: Any

    // 是否持久化
    val durable: Boolean

    // 是否自动删除
    val autoDelete: Boolean
}