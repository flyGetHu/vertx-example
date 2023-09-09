package com.vertx.rabbitmq.enums

import com.vertx.common.model.User

/**
 * rabbitmq交换机枚举
 * @property type 交换机类型
 * @property exchanger 交换机名称
 */
enum class RabbitMqExChangeEnum(
    // 交换机类型
    val type: RabbitMqExChangeTypeEnum,
    // 交换机名称
    val exchanger: String,
    // 消息类型
    val messageType: Any = String::class.java,
    // 是否持久化
    val durable: Boolean = true,
    // 是否自动删除
    val autoDelete: Boolean = false,
) {
    /**
     * 默认交换机
     */
    DEFAULT(RabbitMqExChangeTypeEnum.DEFAULT, ""),

    TESTRabbitMqExChangeEnum(RabbitMqExChangeTypeEnum.FANOUT, "test", User::class.java, true, false)
}