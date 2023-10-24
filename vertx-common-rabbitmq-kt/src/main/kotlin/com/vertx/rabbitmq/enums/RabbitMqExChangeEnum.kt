package com.vertx.rabbitmq.enums

import com.vertx.common.model.User

/**
 * rabbitmq交换机枚举
 * @property type 交换机类型
 * @property exchanger 交换机名称
 */
enum class RabbitMqExChangeEnum(
    // 交换机类型
    override val type: RabbitMqExChangeTypeEnum,
    // 交换机名称
    override val exchanger: String,
    // 消息类型
    override val messageType: Any = String::class.java,
    // 是否持久化
    override val durable: Boolean = true,
    // 是否自动删除
    override val autoDelete: Boolean = false,
) : IRabbitMqExChangeEnum {
    /**
     * 默认交换机
     */
    DEFAULT(RabbitMqExChangeTypeEnum.DEFAULT, ""),

    TESTRabbitMqExChangeEnum(RabbitMqExChangeTypeEnum.FANOUT, "test", User::class.java, true, false),
}